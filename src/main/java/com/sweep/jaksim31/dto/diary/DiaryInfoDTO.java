package com.sweep.jaksim31.dto.diary;

import com.sweep.jaksim31.entity.diary.Diary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryInfoDTO
 * author :  김주현
 * date : 2023-01-12
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-12                김주현             최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryInfoDTO {
    private String diaryId;
    private String userId;
    private String content;
    private LocalDate date;
    private LocalDate modifyDate;
    private String emotion;
    private String[] keywords;
    private String thumbnail;

    public static DiaryInfoDTO of(Diary diary){
        return new DiaryInfoDTO(diary.getId().toString(), diary.getUserId().toString(), diary.getContent(), diary.getDate().toLocalDate(), diary.getModifyDate().toLocalDate(), diary.getEmotion(), diary.getKeywords(), diary.getThumbnail());
    }
}
