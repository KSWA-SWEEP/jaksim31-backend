package com.sweep.jaksim31.dto.diary;

import com.sweep.jaksim31.entity.diary.Diary;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
 * 2023-01-09           김주현             최초 생성
 * 2023-01-11           김주현       Spring Validation 추가
 */

@Data
public class DiaryDTO {
    @NotBlank(message = "사용자를 찾을 수 없습니다.")
    String user_id;
    @NotBlank(message = "내용을 입력해주세요.")
    String content;
    @NotNull(message = "날짜를 입력해주세요.")
    LocalDate date;
    @NotBlank(message = "감정 분석 결과가 없습니다.")
    String emotion;
    @NotNull(message = "키워드가 없습니다.")
    String[] keywords;
    @NotBlank(message = "썸네일이 없습니다.")
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

