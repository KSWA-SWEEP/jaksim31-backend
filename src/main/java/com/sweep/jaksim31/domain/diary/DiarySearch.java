package com.sweep.jaksim31.domain.diary;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * packageName :  com.sweep.jaksim31.domain.diary
 * fileName : DiarySearch
 * author :  김주현
 * date : 2023-02-08
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-02-08              김주현             최초 생성
 */

@Getter
@Setter
@NoArgsConstructor
@Document(indexName ="jaksim31.diary")
public class DiarySearch {
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
    public DiarySearch(String userId, String content, LocalDate date, String emotion, String[] keywords, String thumbnail){
        this.userId = userId;
        this.content = content;
        this.date = date.atTime(9,0);
        this.modifyDate = LocalDate.now().atTime(9,0);
        this.emotion = emotion;
        this.keywords = keywords;
        this.thumbnail = thumbnail;
    }

}
