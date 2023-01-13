package com.sweep.jaksim31.controller.feign;


import com.sweep.jaksim31.controller.feign.config.ExtractKeywordFeignConfig;
import com.sweep.jaksim31.dto.diary.DiaryAnalysisRequest;
import com.sweep.jaksim31.dto.tokakao.ExtractedKeywordResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "extractKeyword", url= "${kakao.extract-keyword.url}", configuration = ExtractKeywordFeignConfig.class)
public interface ExtractKeywordFeign {
    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<ExtractedKeywordResponse> extractKeyword(@RequestBody DiaryAnalysisRequest sentence);
}


