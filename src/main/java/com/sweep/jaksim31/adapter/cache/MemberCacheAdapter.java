package com.sweep.jaksim31.adapter.cache;

import com.sweep.jaksim31.dto.member.MemberInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class MemberCacheAdapter {

    private final RedisTemplate<String, MemberInfoResponse> memberCacheRedisTemplate;
    private final ValueOperations<String, MemberInfoResponse> memberCacheOperation;


    public MemberCacheAdapter(RedisTemplate<String, MemberInfoResponse> memberCacheRedisTemplate) {
        this.memberCacheRedisTemplate = memberCacheRedisTemplate;
        this.memberCacheOperation = memberCacheRedisTemplate.opsForValue();
    }

    public void put(String key, MemberInfoResponse value) {
        memberCacheOperation.set(key, value, Duration.ofSeconds((long)24 * 60 * 60));
    }

    public MemberInfoResponse get(String key) {
        return memberCacheOperation.get(key);
    }

    public void delete(String key) {
        memberCacheRedisTemplate.delete(key);
    }

}
