package com.sweep.jaksim31.dto.diary;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryEmotionStaticsResponse
 * author :  김주현
 * date : 2023-01-16
 * description : 감정 통계 응답 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-16           김주현             최초 생성
 * 2023-01-20           김주현          startDate, endDate(조회기간) 추가
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryEmotionStaticsResponse {
    private List<DiaryEmotionStatics> emotionStatics;
    private LocalDate startDate;
    private LocalDate endDate;

    public static DiaryEmotionStaticsResponse of(List<DiaryEmotionStatics> emotionStatics, LocalDate startDate, LocalDate endDate) {
        return new DiaryEmotionStaticsResponse(emotionStatics,startDate,endDate);
    }
}
