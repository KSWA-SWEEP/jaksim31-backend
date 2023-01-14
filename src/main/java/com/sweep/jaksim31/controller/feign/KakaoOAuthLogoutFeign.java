package com.sweep.jaksim31.controller.feign;

import com.sweep.jaksim31.controller.feign.config.KakaoOAuthLogoutFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoOAuthLogout", url= "${kakao.auth.logout-url}", configuration = KakaoOAuthLogoutFeignConfig.class)
public interface KakaoOAuthLogoutFeign {

    @PostMapping(value = "")
    ResponseEntity<String> requestLogout(@RequestParam("target_id") String loginId);
}
