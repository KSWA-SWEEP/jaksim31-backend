package com.sweep.jaksim31.adapter.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sweep.jaksim31.dto.diary.DiaryEmotionStaticsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class DiaryEmotionStaticsCacheSerializer implements RedisSerializer<DiaryEmotionStaticsResponse> {

    // JSON Mapper
    public static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModules(new JavaTimeModule(), new Jdk8Module());
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    @Override
    public byte[] serialize(DiaryEmotionStaticsResponse diaryEmotionStaticsResponse) throws SerializationException {
        if (Objects.isNull(diaryEmotionStaticsResponse))
            return null;

        try {
            String json = MAPPER.writeValueAsString(diaryEmotionStaticsResponse);
            return json.getBytes(UTF8);
        } catch (JsonProcessingException e) {
            throw new SerializationException("json serialize error", e);
        }
    }

    @Override
    public DiaryEmotionStaticsResponse deserialize(byte[] bytes) throws SerializationException {

        if (Objects.isNull(bytes))
            return null;

        try {
            return MAPPER.readValue(new String(bytes, UTF8), DiaryEmotionStaticsResponse.class);
        } catch (JsonProcessingException e) {
            throw new SerializationException("json deserialize error", e);
        }
    }
}
