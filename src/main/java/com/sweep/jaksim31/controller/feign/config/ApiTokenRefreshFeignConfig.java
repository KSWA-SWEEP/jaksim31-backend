package com.sweep.jaksim31.controller.feign.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ApiTokenRefreshFeignConfig {

        @Value("${kakao.api-token.body}")
        private String requestBody;

        @Bean
        public RequestInterceptor requestInterceptor() {
            return requestTemplate -> {
                requestTemplate.header("Content-Type", "application/json");
                requestTemplate.body(requestBody);
//                requestTemplate.header("X-Object-Meta-content-type", "image/jpeg");
            };
        }
    }
