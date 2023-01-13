package com.sweep.jaksim31.dto.diary;

import com.sweep.jaksim31.domain.diary.Diary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryInfoDTO -> DiarySaveResponse
 * author :  김주현
 * date : 2023-01-12
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-12                김주현             최초 생성
 * 2023-01-13                방근호             클래스 이름 변경
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryInfoResponse {
    private String diaryId;
    private String userId;
    private String content;
    private LocalDate date;
    private LocalDate modifyDate;
    private String emotion;
    private String[] keywords;
    private String thumbnail;

    public static DiaryInfoResponse of(Diary diary){
        return new DiaryInfoResponse(diary.getId().toString(), diary.getUserId().toString(), diary.getContent(), diary.getDate().toLocalDate(), diary.getModifyDate().toLocalDate(), diary.getEmotion(), diary.getKeywords(), diary.getThumbnail());
    }
}
