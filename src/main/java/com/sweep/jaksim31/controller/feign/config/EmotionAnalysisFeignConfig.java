package com.sweep.jaksim31.controller.feign.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


public class EmotionAnalysisFeignConfig {

        @Value("${kakao.emotion-analysis.api-key}")
        private String apiKey;

        @Bean
        public RequestInterceptor requestInterceptor() {
            return requestTemplate -> {
                requestTemplate.header("x-api-key", apiKey);
                requestTemplate.header("Content-Type", "application/json");
            };
        }
    }
