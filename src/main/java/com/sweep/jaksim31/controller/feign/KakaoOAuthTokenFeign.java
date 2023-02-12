package com.sweep.jaksim31.controller.feign;

import com.sweep.jaksim31.controller.feign.config.KakaoOAuthTokenFeignConfig;
import com.sweep.jaksim31.dto.login.KakaoOAuth;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoOAuth", url= "${kakao.auth.token.url}", configuration = KakaoOAuthTokenFeignConfig.class)
public interface KakaoOAuthTokenFeign {

    @PostMapping(value = "")
    ResponseEntity<KakaoOAuth> getAccessToken(@RequestParam("code") String code);
}
