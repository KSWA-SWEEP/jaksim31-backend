package com.sweep.jaksim31.dto.diary;

import lombok.*;

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
 */
@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
public class DiaryEmotionStaticsResponse {
    private List<DiaryEmotionStatics> emotionStatics;
}