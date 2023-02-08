package com.sweep.jaksim31.adapter.cache;

import com.sweep.jaksim31.dto.diary.DiaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class DiaryCacheAdapter {

    private final RedisTemplate<String, DiaryResponse> diaryCacheRedisTemplate;
    private final ValueOperations<String, DiaryResponse> diaryCacheOperation;


    public DiaryCacheAdapter(RedisTemplate<String, DiaryResponse> diaryCacheRedisTemplate) {
        this.diaryCacheRedisTemplate = diaryCacheRedisTemplate;
        this.diaryCacheOperation = diaryCacheRedisTemplate.opsForValue();
    }

    public void put(String key, DiaryResponse value) {
        diaryCacheOperation.set(key, value, Duration.ofSeconds((long)24 * 60 * 60));
    }

    public DiaryResponse get(String key) {
        return diaryCacheOperation.get(key);
    }

    public void delete(String key) {
        diaryCacheRedisTemplate.delete(key);
    }

}
