package com.sweep.jaksim31.exception.type;

import com.sweep.jaksim31.exception.BaseExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * packageName :  com.sweep.jaksim31.exception.type
 * fileName : DiaryExceptionHandler
 * author :  김주현
 * date : 2023-01-11
 * description : Diary 관련 예외 Type 정의
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-11                김주현             최초 생성
 * 2023-01-12                김주현            NOT_FOUND_DIARY 추가
 * 2023-01-15                방근호            DELETE_NOT_FOUND_USER 추가
 */
@Getter
public enum DiaryExceptionType implements BaseExceptionType {

    NOT_FOUND_DIARY("NOT_FOUND_DIARY","일기를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_DIARY("DUPLICATE_DIARY", "이미 일기가 존재합니다.",HttpStatus.BAD_REQUEST),
    WRONG_DATE("WRONG_DATE", "잘못 된 날짜입니다.",HttpStatus.BAD_REQUEST),
    // 삭제 메소드 수행 시 존재 하지 않을 경우 200 응답
    DELETE_NOT_FOUND_DIARY("ALREADY_NOT_EXIST_DIARY", "존재하지 않는 일기입니다.", HttpStatus.OK);

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
