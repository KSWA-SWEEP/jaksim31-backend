package com.sweep.jaksim31.config.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sweep.jaksim31.adapter.RestPage;
import com.sweep.jaksim31.adapter.cache.DiaryCacheSerializer;
import com.sweep.jaksim31.adapter.cache.DiaryEmotionStaticsCacheSerializer;
import com.sweep.jaksim31.adapter.cache.DiaryPagingCacheSerializer;
import com.sweep.jaksim31.adapter.cache.MemberCacheSerializer;
import com.sweep.jaksim31.dto.diary.DiaryEmotionStaticsResponse;
import com.sweep.jaksim31.dto.diary.DiaryInfoResponse;
import com.sweep.jaksim31.dto.diary.DiaryResponse;
import com.sweep.jaksim31.dto.member.MemberInfoResponse;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.SocketOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
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


@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;
    private final RedisInfo info;

    @Profile("local")
    @Bean("redisConnectionFactory")
    public RedisConnectionFactory basicCacheRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);

        final SocketOptions socketOptions = SocketOptions.builder().connectTimeout(Duration.ofSeconds(10)).build();
        final ClientOptions clientOptions = ClientOptions.builder().socketOptions(socketOptions).build();

        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofSeconds(5))
                .shutdownTimeout(Duration.ZERO)
                .build();

        return new LettuceConnectionFactory(configuration, lettuceClientConfiguration);
    }
    @Profile("prod")
    @Bean("redisConnectionFactory")
    public LettuceConnectionFactory redisConnectionFactory(){
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)	// replica?????? ??????????????? ????????? replica?????? ???????????? ?????? ?????? Master?????? ?????????
                .build();
        // replica ??????
        RedisStaticMasterReplicaConfiguration slaveConfig = new RedisStaticMasterReplicaConfiguration(info.getMaster().getHost(), info.getMaster().getPort());
        // ????????? slave ?????? ??? ??????
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

    @Bean
    public RedisTemplate<String, DiaryEmotionStaticsResponse> diaryEmotionStaticsCacheRedisTemplate() {
        RedisTemplate<String, DiaryEmotionStaticsResponse> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new DiaryEmotionStaticsCacheSerializer());

        return redisTemplate;
    }


    //     jackson LocalDateTime mapper
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // timestamp ?????? ??????????????? ??????
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModules(new JavaTimeModule(), new Jdk8Module()); // LocalDateTime ????????? ?????? ?????? ?????????

        return mapper;
    }

    @Bean
    public CacheManager cacheManager() {
        Jackson2JsonRedisSerializer<DiaryInfoResponse> serializer = new Jackson2JsonRedisSerializer<>(objectMapper().constructType(new TypeReference<PageImpl<DiaryInfoResponse>>(){}));
        serializer.setObjectMapper(objectMapper());

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig() // ????????? ?????? ??????
                .disableCachingNullValues()
                .entryTtl(Duration.ofHours(1)) // ?????? ????????? ???????????? ??????
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // ??? ?????????
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()))); // ????????? ?????????

        Map<String, RedisCacheConfiguration> configurations = new HashMap<>();  // ?????? ?????? ??????, ????????? ????????? ??? ?????? configuration ??????
        configurations.put("refreshCache", defaultConfig.entryTtl(Duration.ofMinutes(30))); // 30???
        configurations.put("diaryCache", defaultConfig.entryTtl(Duration.ofDays(1))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new DiaryCacheSerializer()))); // 30???
        configurations.put("diaryPagingCache", defaultConfig.entryTtl(Duration.ofDays(1))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new DiaryPagingCacheSerializer()))); // 30???
        configurations.put("memberCache", defaultConfig.entryTtl(Duration.ofDays(1))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new MemberCacheSerializer()))); // 30???

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory())
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configurations)
                .build();
    }
}