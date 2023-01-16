package com.sweep.jaksim31.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * packageName :  com.sweep.jaksim31.utils
 * fileName : HeaderUtil
 * author :  방근호
 * date : 2023-01-09
 * description : HeaderUtil 설정 관련 클래스
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 *
 */
public class HeaderUtil {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    // 해당 요청의 토큰 값을 가져오는 함수.
    public static String getAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_AUTHORIZATION);

        if (headerValue == null) {
            return null;
        }

        if (headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}

