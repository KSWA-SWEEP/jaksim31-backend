package com.sweep.jaksim31.dto.tokakao;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * packageName :  com.sweep.jaksim31.dto.tokakao
 * fileName : EmotionAnalysisRequest
 * author :  방근호
 * date : 2023-01-13
 * description : 카카오 감정 분석 api 호출을 위한 dto
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */
@Data
@AllArgsConstructor
public class EmotionAnalysisRequest {
    private String msg;
}
