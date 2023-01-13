package com.sweep.jaksim31.dto.diary;
import lombok.*;

import java.util.List;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryAnalysisResponse
 * author :  방근호
 * date : 2023-01-13
 * description : Diary 키워드 추출 및 감정 분석을 위한 응답 dto
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */


@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
public class DiaryAnalysisResponse {

    private List<String> koreanKeywords;
    private List<String> englishKeywords;
    private String koreanEmotion;
    private String englishEmotion;

}
