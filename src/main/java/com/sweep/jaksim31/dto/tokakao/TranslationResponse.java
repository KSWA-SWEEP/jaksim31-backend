package com.sweep.jaksim31.dto.tokakao;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * packageName :  com.sweep.jaksim31.dto.tokakao
 * fileName : TranslationResponse
 * author :  방근호
 * date : 2023-01-13
 * description : 카카오 문장 번역 api 응답을 위한 dto
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */

@Getter
@RequiredArgsConstructor
public class TranslationResponse {

    List<List<String>> input;
    List<List<String>> output;

}
