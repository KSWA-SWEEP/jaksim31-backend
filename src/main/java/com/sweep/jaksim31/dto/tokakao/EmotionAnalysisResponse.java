package com.sweep.jaksim31.dto.tokakao;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * packageName :  com.sweep.jaksim31.dto.tokakao
 * fileName : EmotionAnalysisResponse
 * author :  방근호
 * date : 2023-01-13
 * description : 카카오 감정 분석 api 응답을 위한 dto
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */

@Data
@AllArgsConstructor
public class EmotionAnalysisResponse {

//    private List<AnalysisResult> ;

    private String value;
    private double prob;

}
