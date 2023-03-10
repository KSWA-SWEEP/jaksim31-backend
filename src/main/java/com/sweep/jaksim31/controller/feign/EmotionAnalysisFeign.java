package com.sweep.jaksim31.controller.feign;


import com.sweep.jaksim31.controller.feign.config.EmotionAnalysisFeignConfig;
import com.sweep.jaksim31.dto.tokakao.EmotionAnalysisRequest;
import org.json.simple.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "emotionAnalysis", url= "${kakao.emotion-analysis.url}", configuration = EmotionAnalysisFeignConfig.class)
public interface EmotionAnalysisFeign {
    @PostMapping
    ResponseEntity<JSONObject> emotionAnalysis(@RequestBody EmotionAnalysisRequest emotionAnalysisRequest);
}


