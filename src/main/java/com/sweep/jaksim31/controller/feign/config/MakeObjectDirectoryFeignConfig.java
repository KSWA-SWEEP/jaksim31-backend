package com.sweep.jaksim31.controller.feign.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;


public class MakeObjectDirectoryFeignConfig {

        public static String authToken;

        @Bean
        public RequestInterceptor requestInterceptor() {
            return requestTemplate -> {
                requestTemplate.header("X-Auth-Token", authToken.toString());
                requestTemplate.header("Content-Type", "application/directory");
//                System.out.println(requestTemplate.toString());
//                System.out.println(requestTemplate.body());
            };
        }
    }
