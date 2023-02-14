package com.sweep.jaksim31.adapter.cache;

import com.google.common.collect.Lists;
import com.sweep.jaksim31.dto.diary.DiaryEmotionStaticsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

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

    public void findAndDelete(String key){
        final ScanOptions options = ScanOptions.scanOptions().count(10).match("*"+key+"*").build();

        List<String> keys = diaryEmotionStaticsCacheRedisTemplate.execute((RedisCallback<List<String>>) connection -> {
            List<String> cacheKeys = Lists.newArrayList();
            Cursor<byte[]> cursor = (connection).scan(options);
            while (cursor.hasNext()) {
                String value = new String(cursor.next());
                cacheKeys.add(value);
            }
            return cacheKeys;
        });
        assert keys != null;
        diaryEmotionStaticsCacheRedisTemplate.delete(keys);
    }

}
