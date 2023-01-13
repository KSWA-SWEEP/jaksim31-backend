package com.sweep.jaksim31.controller.feign.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


public class UnsplashSearchFeignConfig {

        @Value("${unsplash.api-key}")
        private String apiKey;
        public static String searchKeyword;

        @Bean
        public RequestInterceptor requestInterceptor() {
            return requestTemplate -> {
                requestTemplate.query("client_id", apiKey);
                requestTemplate.query("query", searchKeyword);
//                System.out.println(requestTemplate.toString());
//                System.out.println(requestTemplate.body());
            };
        }
    }
