package com.sweep.jaksim31.auth;

import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.JwtExceptionType;
import com.sweep.jaksim31.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * packageName :  com.sweep.jaksim31.auth
 * fileName : JwtAuthenticationEntryPoint
 * author :  방근호
 * date : 2023-01-13
 * description : 유효하지 않은 접근에 대해 EntryPoint 제공
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 * 2023-01-31           방근호,김주현       Error 시 Cookie 삭제
 */

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException , ServletException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        CookieUtil.resetDefaultCookies(response);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.println("{\"errorCode\": \"NO_AUTHORIZATION\", \"message\" : \"인증정보가 없습니다.\"}");

    }
}