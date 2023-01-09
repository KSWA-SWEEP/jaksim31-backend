package com.sweep.jaksim31.util.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 필요한 권한이 없이 접근하려 할때 403
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        log.debug("JwtAccessDeniedHandler CALL!");
        out.println("{\"error\": \"AUTH_FORBIDDEN\", \"message\" : \"권한이 없습니다.\"}");
    }
}
