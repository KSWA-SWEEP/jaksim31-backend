package com.sweep.jaksim31.service;

import com.sweep.jaksim31.dto.login.LoginRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;


/**
 * packageName :  com.sweep.jaksim31.service
 * fileName : MemberService
 * author :  방근호
 * date : 2023-01-09
 * description : Member Service 인터페이스
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-15           방근호             카카오 인증 서비스 추가로 인해 수정
 */

public interface MemberService {
    String login(LoginRequest loginRequest, HttpServletResponse response) throws URISyntaxException;
    String logout(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException;
}
