package com.sweep.jaksim31.controller.feign;

import com.sweep.jaksim31.controller.feign.config.KakaoOAuthUserInfoFeignConfig;
import com.sweep.jaksim31.dto.login.KakaoProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoOAuthUserInfo", url= "${kakao.auth.user-info.url}", configuration = KakaoOAuthUserInfoFeignConfig.class)
public interface KakaoOAuthInfoFeign {

    @PostMapping(value = "")
    ResponseEntity<KakaoProfile> getUserInfo(@RequestHeader("Authorization") String accessToken);
}
