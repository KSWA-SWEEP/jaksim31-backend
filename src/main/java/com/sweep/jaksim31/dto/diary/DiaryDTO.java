package com.sweep.jaksim31.dto.diary;

import com.sweep.jaksim31.entity.diary.Diary;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDate;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryDto
 * author :  김주현
 * date : 2023-01-09
 * description : Diary 데이터를 주고받기 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09                김주현             최초 생성
 */

@Data
public class DiaryDTO {
    String user_id;
    String content;
    LocalDate date;
    String emotion;
    String[] keywords;
    String thumbnail;

    @Builder
    public DiaryDTO(String user_id, String content, LocalDate date, String emotion, String[] keywords, String thumbnail){
        this.user_id = user_id;
        this.content = content;
        this.date = date;
        this.emotion = emotion;
        this.keywords = keywords;
        this.thumbnail = thumbnail;
    }

    public Diary toEntity() {
        return Diary.builder()
                .userId(new ObjectId(user_id))
                .content(content)
                .date(date)
                .emotion(emotion)
                .keywords(keywords)
                .thumbnail(thumbnail)
                .build();
    }
}

