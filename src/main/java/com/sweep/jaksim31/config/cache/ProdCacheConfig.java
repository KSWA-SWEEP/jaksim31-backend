package com.sweep.jaksim31.config.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sweep.jaksim31.adapter.RestPage;
import com.sweep.jaksim31.adapter.cache.DiaryCacheSerializer;
import com.sweep.jaksim31.adapter.cache.DiaryPagingCacheSerializer;
import com.sweep.jaksim31.adapter.cache.MemberCacheSerializer;
import com.sweep.jaksim31.dto.diary.DiaryInfoResponse;
import com.sweep.jaksim31.dto.diary.DiaryResponse;
import com.sweep.jaksim31.dto.member.MemberInfoResponse;
import io.lettuce.core.ReadFrom;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Profile("prod")
@Configuration
@RequiredArgsConstructor
public class ProdCacheConfig {
    private final RedisInfo info;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)	// replica에서 우선적으로 읽지만 replica에서 읽어오지 못할 경우 Master에서 읽어옴
                .build();
        // replica 설정
        RedisStaticMasterReplicaConfiguration slaveConfig = new RedisStaticMasterReplicaConfiguration(info.getMaster().getHost(), info.getMaster().getPort());
        // 설정에 slave 설정 값 추가
        info.getSlaves().forEach(slave -> slaveConfig.addNode(slave.getHost(), slave.getPort()));
        return new LettuceConnectionFactory(slaveConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, RestPage<DiaryInfoResponse>> diaryPageCacheRedisTemplate() {
        RedisTemplate<String, RestPage<DiaryInfoResponse>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new DiaryPagingCacheSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, DiaryResponse> diaryCacheRedisTemplate() {
        RedisTemplate<String, DiaryResponse> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new DiaryCacheSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, String> refreshTokenCacheRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, MemberInfoResponse> memberCacheRedisTemplate() {
        RedisTemplate<String, MemberInfoResponse> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new MemberCacheSerializer());

        return redisTemplate;
    }


    //     jackson LocalDateTime mapper
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // timestamp 형식 안따르도록 설정
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModules(new JavaTimeModule(), new Jdk8Module()); // LocalDateTime 매핑을 위해 모듈 활성화

        return mapper;
    }

    @Bean
    public CacheManager cacheManager() {
        Jackson2JsonRedisSerializer<DiaryInfoResponse> serializer = new Jackson2JsonRedisSerializer<>(objectMapper().constructType(new TypeReference<PageImpl<DiaryInfoResponse>>(){}));
        serializer.setObjectMapper(objectMapper());

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig() // 레디스 캐시 설정
                .disableCachingNullValues()
                .entryTtl(Duration.ofHours(1)) // 캐시 데이터 유효기간 설정
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // 키 직렬화
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()))); // 데이터 직렬화

        Map<String, RedisCacheConfiguration> configurations = new HashMap<>();  // 캐시 이름 저장, 캐시를 설정할 수 있는 configuration 설정
        configurations.put("refreshCache", defaultConfig.entryTtl(Duration.ofMinutes(30))); // 30분
        configurations.put("diaryCache", defaultConfig.entryTtl(Duration.ofDays(1))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new DiaryCacheSerializer()))); // 30분
        configurations.put("diaryPagingCache", defaultConfig.entryTtl(Duration.ofDays(1))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new DiaryPagingCacheSerializer()))); // 30분
        configurations.put("memberCache", defaultConfig.entryTtl(Duration.ofDays(1))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new MemberCacheSerializer()))); // 30분

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory())
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configurations)
                .build();
    }
}