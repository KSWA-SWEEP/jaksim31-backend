package com.sweep.jaksim31.dto.diary;


import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryAnalysisRequest
 * author :  방근호
 * date : 2023-01-13
 * description : Diary 키워드 추출 및 감정 분석을 위한 요청 dto
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */
@Getter
@Setter
@RequiredArgsConstructor
public class DiaryAnalysisRequest {

    private List<String> sentences;
    @Hidden
    private String lang = "en";

}
