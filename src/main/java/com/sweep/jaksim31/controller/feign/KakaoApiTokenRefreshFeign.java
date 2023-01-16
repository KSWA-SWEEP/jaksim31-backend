package com.sweep.jaksim31.controller.feign;

import com.sweep.jaksim31.controller.feign.config.KakaoApiTokenRefreshFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name = "apiToken", url = "${kakao.api-token.url}", configuration = KakaoApiTokenRefreshFeignConfig.class)
public interface KakaoApiTokenRefreshFeign {

    @PostMapping("")
    ResponseEntity<String> refreshApiToken();
}


