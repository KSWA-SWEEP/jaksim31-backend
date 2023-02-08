package com.sweep.jaksim31.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * packageName :  com.sweep.jaksim31.enums
 * fileName : ThirdPartyExceptionType
 * author :  방근호
 * date : 2023-01-13
 * description : KIC api 관련 예외 종류
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13            방근호             최초 생성
 * 2023-01-16            방근호             예외 종류 추가 및 이름 변경
 */

@Getter
public enum ThirdPartyExceptionType implements BaseExceptionType {

    NOT_CREATE_DIRECTORY("NOT_FOUND_DIRECTORY","서버 오류로 인해 업로드에 실패하였습니다.", HttpStatus.BAD_REQUEST),

    NOT_UPLOAD_IMAGE("NOT_UPLOAD_IMAGE","서버 오류로 인해 이미지 업로드에 실패하였습니다.", HttpStatus.BAD_REQUEST),

    NOT_ANALYZE_EMOTION("NOT_EMOTION_ANALYZE","서버 오류로 인해 감정 분석에 실패하였습니다.", HttpStatus.BAD_REQUEST),

    NOT_EXTRACT_KEYWORD("NOT_EXTRACT_KEYWORD","서버 오류로 인해 키워드 추출에 실패하였습니다.", HttpStatus.BAD_REQUEST),

    NOT_TRANSLATE_KEYWORD("NOT_TRANSLATE_KEYWORD","서버 오류로 인해 키워드 번역에 실패하였습니다.", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    ThirdPartyExceptionType(String errorCode, String message, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

}
