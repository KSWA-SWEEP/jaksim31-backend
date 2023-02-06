package com.sweep.jaksim31.auth;

import com.sweep.jaksim31.service.impl.MemberServiceImpl;
import com.sweep.jaksim31.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Objects;

/**
 * packageName :  com.sweep.jaksim31.auth
 * fileName : JwtFilter
 * author :  방근호
 * date : 2023-01-13
 * description : SecurityConfig에 추가할 Filter OncePerRequestFilter를 상속 받아 모든 요청에 대한 JWT 검증이 이루어짐.
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 * 2023-01-30           방근호             인증로직 수정, 토큰 기간 만료 시 자동 reissue
 * 2023-01-31           방근호,김주현       Error 시 Cookie 삭제
 */

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    private final MemberServiceImpl memberService;

    public JwtFilter(TokenProvider tokenProvider, MemberServiceImpl memberService){
        this.tokenProvider = tokenProvider;
        this.memberService = memberService;
    }


    // 실제 필터링 로직은 doFilterInternal 에 들어감
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        System.out.println(request.getServletPath());
        if(request.getServletPath().startsWith("/api/v0") || request.getServletPath().startsWith("/swagger") || request.getServletPath().startsWith("/actuator")) {
            filterChain.doFilter(request, response);
        } else {
            String token = resolveToken(request);
            log.debug("token  = {}",token);
            if(StringUtils.hasText(token)) {
                int flag = tokenProvider.validateToken(token);

                log.debug("flag = {}",flag);
                // 토큰 유효함
                if(flag == 1) {
                    filterChain.doFilter(request, response);

                }else if(flag == 2) { // 토큰 만료
                    filterChain.doFilter(request, memberService.reissue(request, response));
                }else { //잘못된 토큰
                    CookieUtil.resetDefaultCookies(response);

                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    log.debug("doFilterInternal Exception CALL!");
                    out.println("{\"errorCode\": \"BAD_TOKEN\", \"message\" : \"잘못된 토큰 값입니다.\"}");
                }
            }
            else {
                CookieUtil.resetDefaultCookies(response);

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setCharacterEncoding("UTF-8");

                PrintWriter out = response.getWriter();
                out.println("{\"errorCode\": \"EMPTY_TOKEN\", \"message\" : \"토큰 값이 비어있습니다.\"}");
            }
        }
    }

    /**
     *
     * @param token
     * 토큰이 유효한 경우 SecurityContext에 저장
     */
    private void setAuthentication(String token) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Request Header 에서 토큰 정보를 꺼내오기
    private String resolveToken(HttpServletRequest request) {
        // bearer : 123123123123123 -> return 123123123123123123
        Cookie refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(req -> req.getName().equals("atk"))
                .findAny()
                .orElse(null);

        if(Objects.isNull(refreshTokenCookie)) return "";

        return refreshTokenCookie.getValue();
    }
}
