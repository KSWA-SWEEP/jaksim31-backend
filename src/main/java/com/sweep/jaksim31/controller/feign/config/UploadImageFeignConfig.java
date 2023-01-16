package com.sweep.jaksim31.controller.feign.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;


public class UploadImageFeignConfig {

        public static String authToken;

        @Bean
        public RequestInterceptor requestInterceptor() {
            return requestTemplate -> {
                requestTemplate.header("X-Auth-Token", authToken.toString());
                requestTemplate.header("Content-Type", "image/png");
                requestTemplate.header("Transfer-Encoding", "gzip");
//                System.out.println(requestTemplate.toString());
//                System.out.println(requestTemplate.body());
            };
        }
    }
