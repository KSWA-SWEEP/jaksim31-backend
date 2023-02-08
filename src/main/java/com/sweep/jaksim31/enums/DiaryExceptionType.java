package com.sweep.jaksim31.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * packageName :  com.sweep.jaksim31.enums
 * fileName : DiaryExceptionType
 * author :  김주현
 * date : 2023-01-11
 * description : Diary 관련 예외 Type 정의
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-11                김주현             최초 생성
 * 2023-01-12                김주현            NOT_FOUND_DIARY 추가
 * 2023-01-15                방근호            DELETE_NOT_FOUND_USER 추가
 * 2023-01-20                김주현            NO_PERMISSION 추가
 * 2023-01-21                김주현            Validation 관련 Exception type 추가
 */
@Getter
public enum DiaryExceptionType implements BaseExceptionType {

    NOT_FOUND_DIARY("NOT_FOUND_DIARY","일기를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_DIARY("DUPLICATE_DIARY", "이미 일기가 존재합니다.",HttpStatus.BAD_REQUEST),
    WRONG_DATE("WRONG_DATE", "잘못 된 날짜입니다.",HttpStatus.BAD_REQUEST),
    NO_PERMISSION("NO_PERMISSION", "권한이 없습니다.",HttpStatus.FORBIDDEN),
    // 삭제 메소드 수행 시 존재 하지 않을 경우 200 응답
    DELETE_NOT_FOUND_DIARY("ALREADY_NOT_EXIST_DIARY", "존재하지 않는 일기입니다.", HttpStatus.OK),
    // Validation Exception type
    INVALID_ID("INVALID_ID", "잘못 된 ID 값입니다.",HttpStatus.BAD_REQUEST),
    USER_ID_IS_NULL("USER_ID_IS_NULL", "사용자 ID가 입력되지 않았습니다.",HttpStatus.BAD_REQUEST),
    DIARY_ID_IS_NULL("DIARY_ID_IS_NULL", "Diary ID가 입력되지 않았습니다.",HttpStatus.BAD_REQUEST),
    CONTENT_IS_NULL("CONTENT_IS_NULL", "일기 내용이 입력되지 않았습니다.",HttpStatus.BAD_REQUEST),
    DIARY_DATE_IS_NULL("DIARY_DATE_IS_NULL", "날짜가 입력되지 않았습니다.",HttpStatus.BAD_REQUEST),
    EMOTION_IS_NULL("EMOTION_IS_NULL", "감정 분석 결과가 입력되지 않았습니다.",HttpStatus.BAD_REQUEST),
    KEYWORDS_IS_NULL("KEYWORDS_IS_NULL", "키워드가 입력되지 않았습니다.",HttpStatus.BAD_REQUEST),
    THUMBNAIL_IS_NULL("THUMBNAIL_IS_NULL", "썸네일이 입력되지 않았습니다.",HttpStatus.BAD_REQUEST),
    INPUT_SENTENCES_IS_NULL("NO_INPUT_SENTENCES", "분석할 문장이 입력되지 않았습니다.",HttpStatus.BAD_REQUEST);
    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    DiaryExceptionType(String errorCode, String message, HttpStatus httpStatus) {
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
