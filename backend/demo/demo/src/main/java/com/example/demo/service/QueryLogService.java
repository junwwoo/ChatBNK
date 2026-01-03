package com.example.demo.service;

import com.example.demo.dto.QueryLogDto;
import com.example.demo.repository.QueryLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryLogService {

    private final QueryLogRepository repo;

    public QueryLogService(QueryLogRepository repo) {
        this.repo = repo;
    }

    public List<QueryLogDto> latest50() {
        return repo.findTop50ByOrderByIdDesc().stream()
                .map(e -> new QueryLogDto(
                        e.getId(),
                        e.getQueryText(),
                        e.getIntent(),
                        e.isCacheHit(),
                        e.getResultCount(),
                        e.getCreatedAt()
                ))
                .toList();
    }


    public CacheStats stats(String intent) {
    long total = repo.countByIntent(intent);
    long hit = repo.countByIntentAndCacheHitTrue(intent);
    double hitRate = total == 0 ? 0.0 : (double) hit / (double) total;

    return new CacheStats(intent, total, hit, hitRate);
  }
  public record CacheStats(String intent, long total, long cacheHit, double hitRate) {}
}
