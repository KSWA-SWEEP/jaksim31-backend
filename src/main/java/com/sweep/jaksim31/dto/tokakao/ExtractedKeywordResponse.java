package com.sweep.jaksim31.dto.tokakao;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * packageName :  com.sweep.jaksim31.dto.tokakao
 * fileName : ExtractedKeywordResponse
 * author :  방근호
 * date : 2023-01-13
 * description : 카카오 키워드 분석 및 추출 api 호출을 위한 dto
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */

@Data
public class ExtractedKeywordResponse {

    List<Result> result;
    double elapsed_time;
    String version;
    int max_candidate_num;

    @Getter
    @ToString
    public static class Result {
        String keyword;
        Double weight;
    }
}
