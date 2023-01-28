package com.sweep.jaksim31.exception.type;

import com.sweep.jaksim31.exception.BaseExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * packageName :  com.sweep.jaksim31.exception.type
 * fileName : MemberExceptionType
 * author :  방근호
 * date : 2023-01-13
 * description : 사용자 관련 예외 Type 정의
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 * 2023-01-16           방근호             DELETE_NOT_FOUND_USER 추가
 */

@Getter
public enum MemberExceptionType implements BaseExceptionType {

    NOT_FOUND_USER("NOT_FOUND_USER","사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_AUTHENTICATION("NOT_FOUND_AUTHENTICATION","인증 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_USER("DUPLICATE_USER","이미 존재하는 사용자입니다.", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD("WRONG_PASSWORD","비밀번호를 잘못 입력하였습니다.", HttpStatus.UNAUTHORIZED),
    NOT_FOUND_USER_ID("NOT_FOUND_USER_ID","사용자 아이디가 비어있습니다.",HttpStatus.BAD_REQUEST),
    NOT_FOUND_LOGIN_ID("NOT_FOUND_LOGIN_ID","로그인 아이디를 입력해주세요",HttpStatus.BAD_REQUEST),
    NOT_FOUND_PASSWORD("NOT_FOUND_PASSWORD","비밀번호를 입력해주세요",HttpStatus.BAD_REQUEST),
    NOT_FOUND_USERNAME("NOT_FOUND_USERNAME","사용자 이름을 입력해주세요",HttpStatus.BAD_REQUEST),
    NOT_FOUND_PROFILE_IMAGE("NOT_FOUND_PROFILE_IMAGE","프로필 이미지를 찾을 수 없습니다.",HttpStatus.BAD_REQUEST),
    NOT_FOUND_NEW_PASSWORD("NOT_FOUND_NEW_PASSWORD","새로운 비밀번호를 입력해주세요",HttpStatus.BAD_REQUEST),
    LOGOUT_MEMBER("LOGOUT_MEMBER","로그아웃된 사용자입니다.",HttpStatus.BAD_REQUEST),

    SESSION_EXPIRED("SESSION_EXPIRED","세션이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    DELETE_NOT_FOUND_USER("ALREADY_NOT_EXIST_MEMBER", "존재하지 않는 사용자입니다.", HttpStatus.SEE_OTHER);


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
