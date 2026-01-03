package com.example.demo.service;

import com.example.demo.domain.QueryLog;
import com.example.demo.dto.BusanProductSummary;
import com.example.demo.dto.ProductCategory;
import com.example.demo.repository.QueryLogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ChatQueryService {

    private final IntentService intentService;
    private final BusanBankCrawlService busanCrawlService;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final QueryLogRepository queryLogRepository;

    public ChatQueryService(
            IntentService intentService,
            BusanBankCrawlService busanCrawlService,
            StringRedisTemplate redis,
            ObjectMapper objectMapper,
            QueryLogRepository queryLogRepository
    ) {
        this.intentService = intentService;
        this.busanCrawlService = busanCrawlService;
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.queryLogRepository = queryLogRepository;
    }

    public ChatResponse handle(String q, int limit) {
        ProductCategory category = intentService.classify(q);

        if (category == ProductCategory.UNKNOWN) {
            queryLogRepository.save(new QueryLog(q, category.name(), false, 0));
            return ChatResponse.message(
                    category.name(),
                    "원하시는 항목을 골라 말해줘요: 예금 / 출금(입출금) / 대출 / 카드\n예) '예금 알려줘', '입출금 통장 추천', '대출 상품 알려줘', '카드 추천'"
            );
        }

        String cacheKey = "busan:" + category.name() + ":list:limit=" + limit;

        String cachedJson = redis.opsForValue().get(cacheKey);
        boolean cacheHit = cachedJson != null;

        List<BusanProductSummary> data;

        if (cacheHit) {
            try {
                data = objectMapper.readValue(
                        cachedJson,
                        new TypeReference<List<BusanProductSummary>>() {}
                );
            } catch (Exception e) {
                cacheHit = false;
                data = busanCrawlService.crawlProducts(category, limit);
            }
        } else {
            data = busanCrawlService.crawlProducts(category, limit);
        }

        if (!cacheHit) {
            try {
                String json = objectMapper.writeValueAsString(data);
                redis.opsForValue().set(cacheKey, json, Duration.ofMinutes(10));
            } catch (Exception ignored) {
                // 캐시는 실패해도 서비스는 돌아가게
            }
        }

        queryLogRepository.save(new QueryLog(q, category.name(), cacheHit, data.size()));
        return ChatResponse.products(category.name(), cacheHit, data);
    }

    public record ChatResponse(
            String category,
            boolean cacheHit,
            String message,
            List<BusanProductSummary> products
    ) {
        static ChatResponse products(String category, boolean cacheHit, List<BusanProductSummary> products) {
            return new ChatResponse(category, cacheHit, null, products);
        }

        static ChatResponse message(String category, String message) {
            return new ChatResponse(category, false, message, null);
        }
    }
}
