package com.sweep.jaksim31.controller.feign.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

import java.net.URISyntaxException;

public class KakaoOAuthUserInfoFeignConfig {
        @Bean
        public RequestInterceptor requestInterceptor() {
            return requestTemplate -> requestTemplate.header("Content-Type", "application/x-www-form-urlencoded");
        }
    }
