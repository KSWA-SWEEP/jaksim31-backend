package com.sweep.jaksim31.util.exceptionhandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberExceptionType implements BaseExceptionType{

    NOT_FOUND_USER("NOT_FOUND_USER","사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_AUTHENTICATION("NOT_FOUND_AUTHENTICATION","인증 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_USER("DUPLICATE_USER","이미 존재하는 사용자입니다.", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD("WRONG_PASSWORD","비밀번호를 잘못 입력하였습니다.", HttpStatus.UNAUTHORIZED),
    NOT_FOUND_PASSWORD("NOT_FOUND_PASSWORD","비밀번호를 입력해주세요",HttpStatus.BAD_REQUEST),
    LOGOUT_MEMBER("LOGOUT_MEMBER","로그아웃된 사용자입니다.",HttpStatus.BAD_REQUEST),

    SESSION_EXPIRED("SESSION_EXPIRED","세션이 만료되었습니다.", HttpStatus.UNAUTHORIZED);


    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    MemberExceptionType(String errorCode,String message,HttpStatus httpStatus) {
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
