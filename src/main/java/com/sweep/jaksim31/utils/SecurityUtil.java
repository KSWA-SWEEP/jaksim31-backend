package com.sweep.jaksim31.utils;

import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.MemberExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * packageName :  com.sweep.jaksim31.utils
 * fileName : SecurityUitl
 * author :  방근호
 * date : 2023-01-09
 * description : 인증관련 클래스(토큰 기반 현재 유저의 아이디를 찾습니다.)
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 *
 */

@Slf4j
public class SecurityUtil {
    private SecurityUtil() { } // 인스턴스 생성 X

    /**
     * @return SecurityContext에 저장되어 있는 유저 아이디를 반환함
     */
    public static String getCurrentMemberLoginId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication.getName() == null) {
            throw new BizException(MemberExceptionType.NOT_FOUND_AUTHENTICATION);
        }

        return authentication.getName();
    }

}