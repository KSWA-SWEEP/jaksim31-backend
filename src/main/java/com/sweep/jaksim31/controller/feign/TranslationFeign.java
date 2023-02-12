package com.sweep.jaksim31.controller.feign;


import com.sweep.jaksim31.controller.feign.config.TranslationFeignConfig;
import com.sweep.jaksim31.dto.tokakao.TranslationRequest;
import com.sweep.jaksim31.dto.tokakao.TranslationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "translation", url= "${kakao.translation.url}", configuration = TranslationFeignConfig.class)
public interface TranslationFeign {
    @PostMapping
    ResponseEntity<TranslationResponse> translation(@RequestBody TranslationRequest translationRequest);
}


