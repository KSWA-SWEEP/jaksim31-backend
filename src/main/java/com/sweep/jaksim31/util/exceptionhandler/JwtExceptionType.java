package com.sweep.jaksim31.util.exceptionhandler;

import org.springframework.http.HttpStatus;

public enum JwtExceptionType implements BaseExceptionType{

    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN","유효하지 않은 리프레시 토큰입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_TOKEN("INVALID_ACCESS_TOKEN","유효하지 않은 엑세스 토큰입니다.", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_EXPIRED("ACCESS_TOKEN_EXPIRED","엑세스 토큰의 유효기간이 만료되었습니다.",HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED","리프레시 토큰의 유효기간이 만료되었습니다.",HttpStatus.BAD_REQUEST),
    BAD_TOKEN("BAD_TOKEN","잘못된 토큰 값입니다.",HttpStatus.BAD_REQUEST),
    EMPTY_TOKEN("EMPTY_TOKEN","토큰 값이 비어있습니다.",HttpStatus.BAD_REQUEST),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    JwtExceptionType(String errorCode, String message, HttpStatus httpStatus) {
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
