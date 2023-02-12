package com.sweep.jaksim31.utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
/**
 * packageName :  com.sweep.jaksim31.utils
 * fileName : JsonUtil
 * author :  방근호
 * date : 2023-01-13
 * description : Json을 위한 Util
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13            방근호             최초 생성
 * 2023-01-18            김주현             Time Module 추가
 */
public class JsonUtil {

    private JsonUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

}
