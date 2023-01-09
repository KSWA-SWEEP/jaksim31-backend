package com.sweep.jaksim31.util;

import com.sweep.jaksim31.util.exceptionhandler.BizException;
import com.sweep.jaksim31.util.exceptionhandler.MemberExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {
    private SecurityUtil() { } // 인스턴스 생성 X

    /**
     * @return SecurityContext에 저장되어 있는 유저 아이디를 반환함
     */
    public static String getCurrentMemberEmail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication.getName() == null) {
            throw new BizException(MemberExceptionType.NOT_FOUND_AUTHENTICATION);
        }

        return authentication.getName();
    }

}