package com.example.demo.service;

import com.example.demo.domain.QueryLog;
import com.example.demo.dto.BusanSavingProductSummary;
import com.example.demo.repository.QueryLogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ChatQueryService {
    private final BnkCapitalCrawlService capitalCrawl;

    private final IntentService intentService;
    private final BusanBankCrawlService busanCrawl;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final QueryLogRepository queryLogRepository;

    public ChatQueryService(
        IntentService intentService,
        BusanBankCrawlService busanCrawl,
        BnkCapitalCrawlService capitalCrawl,
        StringRedisTemplate redis,
        ObjectMapper objectMapper,
        QueryLogRepository queryLogRepository
    ) {
        this.intentService = intentService;
        this.busanCrawl = busanCrawl;
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.queryLogRepository = queryLogRepository;
        this.capitalCrawl = capitalCrawl;
    }

    public ChatResponse handle(String q, int limit) {
        IntentService.Intent intent = intentService.classify(q);

        if (intent == IntentService.Intent.SAVING) {
            String cacheKey = "busan:saving:list:limit=" + limit;

            String cachedJson = redis.opsForValue().get(cacheKey);
            boolean cacheHit = cachedJson != null;

            List<BusanSavingProductSummary> data;

            if (cacheHit) {
                // JSON 문자열 -> 타입 있는 DTO 리스트로 복원
                try {
                    data = objectMapper.readValue(
                            cachedJson,
                            new TypeReference<List<BusanSavingProductSummary>>() {}
                    );
                } catch (Exception e) {
                    // 캐시가 깨졌으면 MISS로 처리하고 재생성
                    cacheHit = false;
                    data = busanCrawl.crawlSavingProducts(limit);
                }
            } else {
                data = busanCrawl.crawlSavingProducts(limit);
            }

            if (!cacheHit) {
                try {
                    String json = objectMapper.writeValueAsString(data);
                    redis.opsForValue().set(cacheKey, json, Duration.ofMinutes(10));
                } catch (Exception e) {
                    // 캐시는 실패해도 서비스는 돌아가게
                }
            }

            queryLogRepository.save(new QueryLog(q, intent.name(), cacheHit, data.size()));
            return ChatResponse.saving(intent.name(), cacheHit, data);
        }

        queryLogRepository.save(new QueryLog(q, intent.name(), false, 0));
        return ChatResponse.message(intent.name(), "아직은 예금/적금(저축)만 지원해요. 예: '예금 알려줘'");
    }

    public record ChatResponse(String intent, boolean cacheHit, String message, List<BusanSavingProductSummary> products) {
        static ChatResponse saving(String intent, boolean cacheHit, List<BusanSavingProductSummary> products) {
            return new ChatResponse(intent, cacheHit, null, products);
        }
        static ChatResponse message(String intent, String message) {
            return new ChatResponse(intent, false, message, null);
        }
    }
}
