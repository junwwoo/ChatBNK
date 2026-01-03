package com.example.demo.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "query_log")
public class QueryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String queryText;

    @Column(nullable = false, length = 50)
    private String intent; // SAVING / LOAN / UNKNOWN

    @Column(nullable = false)
    private boolean cacheHit;

    @Column(nullable = false)
    private int resultCount;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected QueryLog() {}

    public QueryLog(String queryText, String intent, boolean cacheHit, int resultCount) {
        this.queryText = queryText;
        this.intent = intent;
        this.cacheHit = cacheHit;
        this.resultCount = resultCount;
    }

    public Long getId() { return id; }

    public String getQueryText() { return queryText; }
    public String getIntent() { return intent; }
    public boolean isCacheHit() { return cacheHit; }
    public int getResultCount() { return resultCount; }
    public java.time.Instant getCreatedAt() { return createdAt; }

}
