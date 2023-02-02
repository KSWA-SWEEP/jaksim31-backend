package com.sweep.jaksim31.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * packageName :  com.sweep.jaksim31.enums
 * fileName : AuthorityExceptionType
 * author :  방근호
 * date : 2023-01-13
 * description : 사용자 권한 관련 예외 Type 정의
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13                방근호             최초 생성
 *
 */

@Getter
public enum AuthorityExceptionType implements BaseExceptionType {
    NOT_FOUND_AUTHORITY("NOT_FOUND_AUTHORITY","존재하지 않는 권한 입니다.", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    AuthorityExceptionType(String errorCode, String message, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}