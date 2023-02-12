package com.sweep.jaksim31.controller.feign.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class KakaoOAuthLogoutFeignConfig {
        @Value("${kakao.auth.admin-key}")
        private String adminKey;

        @Bean
        public RequestInterceptor requestInterceptor() {
            return requestTemplate -> {
                requestTemplate.header("Content-Type", "application/x-www-form-urlencoded");
                requestTemplate.header("Authorization", "KakaoAK " + adminKey);
                requestTemplate.query("target_id_type", "user_id");
            };
        }
    }
