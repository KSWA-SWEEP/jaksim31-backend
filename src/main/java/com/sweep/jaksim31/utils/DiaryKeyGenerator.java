package com.sweep.jaksim31.utils;

import com.sweep.jaksim31.dto.diary.DiarySaveRequest;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class DiaryKeyGenerator implements KeyGenerator {

    private final String PREFIX = "DIARY::";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Object generate(Object target, Method method, Object... params) {

        return Arrays.stream(params)
//                .filter(param -> param instanceof String)
                .filter(param -> param instanceof String)
                .findFirst()
//                .map(DiarySaveRequest.class::cast)
                .map(param -> PREFIX + param + "::")
                .orElse(SimpleKeyGenerator.generateKey(params).toString());
    }
}
