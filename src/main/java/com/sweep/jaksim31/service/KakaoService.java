package com.sweep.jaksim31.service;

import com.sweep.jaksim31.dto.login.KaKaoInfoDTO;
import com.sweep.jaksim31.dto.login.LoginReqDTO;
import com.sweep.jaksim31.dto.member.MemberRespDTO;
import com.sweep.jaksim31.dto.token.TokenDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

public interface KakaoService {

    @Transactional
    ResponseEntity<MemberRespDTO> kakaosignup(KaKaoInfoDTO memberRequestDto);

    @Transactional
    ResponseEntity<TokenDTO> kakaologin(KaKaoInfoDTO loginReqDTO, HttpServletResponse response);

    String getAccessToken(String code);
    KaKaoInfoDTO getUserInfo(String accessToken);
    void kakaoLogout(String accessToken);

}