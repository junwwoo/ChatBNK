package com.example.demo.dto;

import java.time.Instant;

public record QueryLogDto(
        Long id,
        String queryText,
        String intent,
        boolean cacheHit,
        int resultCount,
        Instant createdAt
) {}
