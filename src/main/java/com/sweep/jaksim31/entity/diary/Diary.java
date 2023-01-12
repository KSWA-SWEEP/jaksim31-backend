package com.sweep.jaksim31.entity.diary;

import com.sweep.jaksim31.dto.diary.DiaryDTO;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.LocalDate.now;

/**
 * packageName :  com.sweep.jaksim31.entity.diary
 * fileName : Diary
 * author :  김주현
 * date : 2023-01-09
 * description : Diary 객체
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09                김주현             최초 생성
 */

@Getter
@Setter
@NoArgsConstructor
@Document(collection="diary")
public class Diary {
    @Id
    @Field("_id")
    private ObjectId id;
    @Field("user_id")
    private ObjectId userId;
    @Field("content")
    private String content;
    @Field("date")
    private LocalDateTime date;
    @Field("modify_date")
    private LocalDateTime modifyDate;
    @Field("emotion")
    private String emotion;
    @Field("keywords")
    private String[] keywords;
    @Field("thumbnail")
    private String thumbnail;
    @Builder
    public Diary(ObjectId userId, String content, LocalDate date, String emotion, String[] keywords, String thumbnail){
        this.userId = userId;
        this.content = content;
        this.date = date.atTime(9,0);
        this.modifyDate = now().atTime(9,0);
        this.emotion = emotion;
        this.keywords = keywords;
        this.thumbnail = thumbnail;
    }

    public Diary(String diaryId, DiaryDTO diaryDto){
        this.id = new ObjectId(diaryId);
        this.userId = new ObjectId(diaryDto.getUser_id());
        this.content = diaryDto.getContent();
        this.date = diaryDto.getDate().atTime(9,0);
        this.modifyDate = LocalDate.now().atTime(9,0);
        this.emotion = diaryDto.getEmotion();
        this.keywords = diaryDto.getKeywords();
        this.thumbnail = diaryDto.getThumbnail();
    }
}

