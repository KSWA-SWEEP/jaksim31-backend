package com.sweep.jaksim31.adapter.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class RefreshTokenCacheAdapter {

    private final RedisTemplate<String, String> refreshTokenCacheRedisTemplate;
    private final ValueOperations<String, String> refreshTokenCacheOperation;


    public RefreshTokenCacheAdapter(RedisTemplate<String, String> refreshTokenCacheRedisTemplate) {
        this.refreshTokenCacheRedisTemplate = refreshTokenCacheRedisTemplate;
        this.refreshTokenCacheOperation = refreshTokenCacheRedisTemplate.opsForValue();
    }

    public void put(String key, String value, Duration duration) {
        refreshTokenCacheOperation.set(key, value, duration);
    }

    public String get(String key) {
        return refreshTokenCacheOperation.get(key);
    }

    public void delete(String key) {
        refreshTokenCacheRedisTemplate.delete(key);
    }

}
