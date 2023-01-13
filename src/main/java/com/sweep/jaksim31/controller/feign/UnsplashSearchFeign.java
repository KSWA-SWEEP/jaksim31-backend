package com.sweep.jaksim31.controller.feign;


import com.sweep.jaksim31.controller.feign.config.UnsplashSearchFeignConfig;
import org.json.simple.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

@FeignClient(name = "unsplash", url = "any-value", configuration = UnsplashSearchFeignConfig.class)
public interface UnsplashSearchFeign {

    @GetMapping("/")
    ResponseEntity<JSONObject> getImageUrl(URI uri);

}


