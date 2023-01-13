package com.sweep.jaksim31.service;

import com.sweep.jaksim31.dto.login.KakaoLoginRequest;
import com.sweep.jaksim31.dto.member.MemberSaveResponse;
import com.sweep.jaksim31.dto.token.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

public interface KakaoService {

    @Transactional
    ResponseEntity<MemberSaveResponse> kakaosignup(KakaoLoginRequest memberRequestDto);

    @Transactional
    ResponseEntity<TokenResponse> kakaologin(KakaoLoginRequest loginReqDTO, HttpServletResponse response);

    String getAccessToken(String code);
    KakaoLoginRequest getUserInfo(String accessToken);
    void kakaoLogout(String accessToken);

}