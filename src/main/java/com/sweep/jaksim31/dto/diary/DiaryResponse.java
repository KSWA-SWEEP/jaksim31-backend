package com.sweep.jaksim31.dto.diary;

import com.sweep.jaksim31.domain.diary.Diary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryResponse
 * author :  김주현
 * date : 2023-01-19
 * description : 일기 정보 전체를 전달하는 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-19           김주현             최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryResponse {
    @Id
    private String diaryId;
    private String userId;
    private String content;
    private LocalDate diaryDate;
    private LocalDate modifyDate;
    private String emotion;
    private String[] keywords;
    private String thumbnail;

    public static DiaryResponse of(Diary diary){
        return new DiaryResponse(diary.getId(), diary.getUserId(), diary.getContent(), diary.getDate().toLocalDate(), diary.getModifyDate().toLocalDate(), diary.getEmotion(), diary.getKeywords(), diary.getThumbnail());
    }
}
