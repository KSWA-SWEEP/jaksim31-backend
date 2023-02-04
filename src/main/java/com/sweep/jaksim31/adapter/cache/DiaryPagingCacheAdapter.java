package com.sweep.jaksim31.adapter.cache;

import com.google.common.collect.Lists;
import com.sweep.jaksim31.adapter.RestPage;
import com.sweep.jaksim31.dto.diary.DiaryInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class DiaryPagingCacheAdapter {

    private final RedisTemplate<String, RestPage<DiaryInfoResponse>> diaryPageCacheRedisTemplate;
    private final ValueOperations<String, RestPage<DiaryInfoResponse>> diaryPageCacheOperation;


    public DiaryPagingCacheAdapter(RedisTemplate<String, RestPage<DiaryInfoResponse>> diaryPageCacheRedisTemplate) {
        this.diaryPageCacheRedisTemplate = diaryPageCacheRedisTemplate;
        this.diaryPageCacheOperation = diaryPageCacheRedisTemplate.opsForValue();
    }

    public void put(String key, RestPage<DiaryInfoResponse> value) {
        diaryPageCacheOperation.set(key, value, Duration.ofSeconds(24 * 60 * 60));
    }

    public RestPage<DiaryInfoResponse> get(String key) {
        return diaryPageCacheOperation.get(key);
    }

    public void delete(String key) {
        diaryPageCacheRedisTemplate.delete(key);
    }

    public void findAndDelete(String key){
        final ScanOptions options = ScanOptions.scanOptions().count(10).match("*"+key+"*").build();

        List<String> keys = diaryPageCacheRedisTemplate.execute((RedisCallback<List<String>>) connection -> {
            List<String> cacheKeys = Lists.newArrayList();
            Cursor<byte[]> cursor = (connection).scan(options);
            while (cursor.hasNext()) {
                String value = new String(cursor.next());
                cacheKeys.add(value);
//      System.out.println("KEYS Evict===========>>>>>>>>>" + value);
            }
            return cacheKeys;
        });
        assert keys != null;
        diaryPageCacheRedisTemplate.delete(keys);
    }

}
