package com.sweep.jaksim31.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * packageName :  com.sweep.jaksim31.enums
 * fileName : SuccessResponseType
 * author :  김주현
 * date : 2023-02-02
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-02-02              김주현             최초 생성
 */
@Getter
public enum SuccessResponseType {
    //Member
    SIGNUP_SUCCESS("회원가입이 완료되었습니다.", HttpStatus.CREATED),
    LOGIN_SUCCESS("로그인이 완료되었습니다.", HttpStatus.OK),
    KAKAO_LOGIN_SUCCESS("로그인이 완료되었습니다.", HttpStatus.SEE_OTHER),
    IS_MEMBER_SUCCESS(" 해당 이메일은 가입하였습니다.", HttpStatus.OK),
    USER_UPDATE_SUCCESS("회원 정보가 정상적으로 변경되었습니다.", HttpStatus.OK),
    CHECK_PW_SUCCESS("비밀번호가 일치합니다.", HttpStatus.OK),
    USER_REMOVE_SUCCESS("정상적으로 회원탈퇴 작업이 처리되었습니다.", HttpStatus.SEE_OTHER),
    LOGOUT_SUCCESS("로그아웃 되었습니다.", HttpStatus.OK),
    KAKAO_LOGOUT_SUCCESS("로그아웃 되었습니다.", HttpStatus.SEE_OTHER),
    //Diary
    DIARY_SAVE_SUCCESS("일기 저장이 완료되었습니다.", HttpStatus.CREATED),
    DIARY_UPDATE_SUCCESS("일기 수정이 완료되었습니다.", HttpStatus.OK),
    DIARY_REMOVE_SUCCESS("일기 삭제가 완료되었습니다.", HttpStatus.OK)
    ;

    private final String message;
    private final HttpStatus httpStatus;

    SuccessResponseType(String message, HttpStatus httpStatus){
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
