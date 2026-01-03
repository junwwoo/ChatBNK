package com.example.demo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisTestService {

    private final StringRedisTemplate redis;

    public RedisTestService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void set(String key, String value) {
        redis.opsForValue().set(key, value, Duration.ofMinutes(10));
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(redis.opsForValue().get(key));
    }
}
