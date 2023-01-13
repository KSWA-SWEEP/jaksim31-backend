package com.sweep.jaksim31.controller.feign;


import com.sweep.jaksim31.controller.feign.config.EmotionAnalysisFeignConfig;
import com.sweep.jaksim31.dto.tokakao.EmotionAnalysisRequest;
import org.json.simple.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "emotionAnalysis", url= "${kakao.emotion-analysis.url}", configuration = EmotionAnalysisFeignConfig.class)
public interface EmotionAnalysisFeign {
    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<JSONObject> emotionAnalysis(@RequestBody EmotionAnalysisRequest emotionAnalysisRequest);
}


