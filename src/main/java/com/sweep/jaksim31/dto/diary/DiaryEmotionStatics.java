package com.sweep.jaksim31.dto.diary;

import lombok.*;
import org.springframework.data.annotation.Id;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryEmotionStatics
 * author :  김주현
 * date : 2023-01-17
 * description : 감정 통계 결과 제공을 위한 class
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-17           김주현             최초 생성
 */
@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
public class DiaryEmotionStatics {
    @Id
    private String emotion; // 감정
    private int countEmotion; // 개수
}
