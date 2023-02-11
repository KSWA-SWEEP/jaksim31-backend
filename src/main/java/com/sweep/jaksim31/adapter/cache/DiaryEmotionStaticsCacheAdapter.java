package com.sweep.jaksim31.adapter.cache;

import com.sweep.jaksim31.dto.diary.DiaryEmotionStaticsResponse;
import com.sweep.jaksim31.dto.diary.DiaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class DiaryEmotionStaticsCacheAdapter {

    private final RedisTemplate<String, DiaryEmotionStaticsResponse> diaryEmotionStaticsCacheRedisTemplate;
    private final ValueOperations<String, DiaryEmotionStaticsResponse> diaryCacheOperation;


    public DiaryEmotionStaticsCacheAdapter(RedisTemplate<String, DiaryEmotionStaticsResponse> diaryEmotionStaticsCacheRedisTemplate) {
        this.diaryEmotionStaticsCacheRedisTemplate = diaryEmotionStaticsCacheRedisTemplate;
        this.diaryCacheOperation = diaryEmotionStaticsCacheRedisTemplate.opsForValue();
    }

    public void put(String key, DiaryEmotionStaticsResponse value) {
        diaryCacheOperation.set(key, value, Duration.ofSeconds((long)24 * 60 * 60));
    }

    public DiaryEmotionStaticsResponse get(String key) {
        return diaryCacheOperation.get(key);
    }

    public void delete(String key) {
        diaryEmotionStaticsCacheRedisTemplate.delete(key);
    }

}
