package com.sweep.jaksim31.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * packageName :  com.sweep.jaksim31.config
 * fileName : WebConfig
 * author :  방근호
 * date : 2023-01-13
 * description : CORS 정책 설정
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 * 2023-02-06           방근호             CORS 정책 수정
 *
 */


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://jaksim31.xyz", "http://jaksim31.xyz")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
