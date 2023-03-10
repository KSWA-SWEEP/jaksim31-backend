package com.sweep.jaksim31.domain.diary;

import com.sweep.jaksim31.dto.diary.DiarySaveRequest;
import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;


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
 * 2023-01-18                김주현      id data type 변경(ObjectId -> String)
 */

@Getter
@Setter
@NoArgsConstructor
@Document(collection="diary")
public class Diary {
    @Id
    private String id;
    private String userId;
    private String content;
    private LocalDateTime date;
    private LocalDateTime modifyDate;
    private String emotion;
    private String[] keywords;
    private String thumbnail;
    @Builder
    public Diary(String userId, String content, LocalDate date, String emotion, String[] keywords, String thumbnail){
        this.userId = userId;
        this.content = content;
        this.date = date.atTime(9,0);
        this.modifyDate = LocalDate.now().atTime(9,0);
        this.emotion = emotion;
        this.keywords = keywords;
        this.thumbnail = thumbnail;
    }

    public Diary(String diaryId, DiarySaveRequest diarySaveRequest){
        this.id = diaryId;
        this.userId = diarySaveRequest.getUserId();
        this.content = diarySaveRequest.getContent();
        this.date = diarySaveRequest.getDate().atTime(9,0);
        this.modifyDate = LocalDate.now().atTime(9,0);
        this.emotion = diarySaveRequest.getEmotion();
        this.keywords = diarySaveRequest.getKeywords();
        this.thumbnail = diarySaveRequest.getThumbnail();
    }

}

