package com.example.demo.api;

import com.example.demo.dto.QueryLogDto;
import com.example.demo.service.QueryLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class QueryLogController {

    private final QueryLogService service;

    public QueryLogController(QueryLogService service) {
        this.service = service;
    }

    @GetMapping("/queries")
    public List<QueryLogDto> latestQueries() {
        return service.latest50();
    }

    @GetMapping("/cache-stats")
    public QueryLogService.CacheStats cacheStats() {
    // 지금은 SAVING만 운영 중이니까 고정
    return service.stats("SAVING");
    }
}
