package com.sweep.jaksim31.dto.diary.extractkeyword;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryAnalysisRequest2
 * author :  방근호
 * date : 2023-02-12
 * description : Diary 키워드 추출 및 감정 분석을 위한 요청 dto
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-02-12           방근호             최초 생성
 *
 */
@Getter
@Setter
@Builder
public class ExtractKeywordRequest {

    @JsonProperty("request_id")
    private static final String REQUEST_ID = "reserved field";

    @JsonProperty("argument")
    private final Argument argument;

}
