package com.sweep.jaksim31.controller.feign.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class KakaoOAuthTokenFeignConfig {
        @Value("${kakao.auth.token.grant_type}")
        private String grantType;
        @Value("${kakao.auth.token.client_id}")
        private String clientId;
        @Value("${kakao.auth.token.redirect_url}")
        private String redirectUrl;


        @Bean
        public RequestInterceptor requestInterceptor() throws URISyntaxException {
            URI encodedRedirectUrl = new URI(redirectUrl);
            return requestTemplate -> {
                requestTemplate.header("Content-Type", "application/x-www-form-urlencoded");
                requestTemplate.query("grant_type", grantType);
                requestTemplate.query("client_id", clientId);
                requestTemplate.query("redirect_uri", String.valueOf(encodedRedirectUrl));
            };
        }
    }
