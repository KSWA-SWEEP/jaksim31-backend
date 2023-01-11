package com.sweep.jaksim31.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sweep.jaksim31.dto.login.KaKaoInfoDTO;
import com.sweep.jaksim31.dto.login.LoginReqDTO;
import com.sweep.jaksim31.dto.member.MemberReqDTO;
import com.sweep.jaksim31.dto.member.MemberRespDTO;
import com.sweep.jaksim31.dto.token.TokenDTO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public interface KakaoService {

    @Transactional
    MemberRespDTO kakaosignup(MemberReqDTO memberRequestDto);

    @Transactional
    TokenDTO kakaologin(LoginReqDTO loginReqDTO, HttpServletResponse response);

    String getAccessToken(String code);
    KaKaoInfoDTO getUserInfo(String accessToken);
    void kakaoLogout(String accessToken);

}