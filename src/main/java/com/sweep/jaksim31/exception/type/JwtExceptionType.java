package com.sweep.jaksim31.exception.type;

import com.sweep.jaksim31.exception.BaseExceptionType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * packageName :  com.sweep.jaksim31.exception.type
 * fileName : JwtExceptionType
 * author :  방근호
 * date : 2023-01-13
 * description : JWT 관련 예외 Type 정의
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13               방근호             최초 생성
 * 2023-01-17               방근호          LOGOUT_EMPTY_TOKEN 3xx로 변경
 *
 */

public enum JwtExceptionType implements BaseExceptionType {

    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN","유효하지 않은 리프레시 토큰입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_TOKEN("INVALID_ACCESS_TOKEN","유효하지 않은 엑세스 토큰입니다.", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_EXPIRED("ACCESS_TOKEN_EXPIRED","엑세스 토큰의 유효기간이 만료되었습니다.",HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED","리프레시 토큰의 유효기간이 만료되었습니다.",HttpStatus.BAD_REQUEST),
    BAD_TOKEN("BAD_TOKEN","잘못된 토큰 값입니다.",HttpStatus.BAD_REQUEST),
    EMPTY_TOKEN("EMPTY_TOKEN","토큰 값이 비어있습니다.",HttpStatus.BAD_REQUEST),
    LOGOUT_EMPTY_TOKEN("LOGOUT_EMPTY_TOKEN","이미 로그아웃된 사용자입니다.",HttpStatus.SEE_OTHER),
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
