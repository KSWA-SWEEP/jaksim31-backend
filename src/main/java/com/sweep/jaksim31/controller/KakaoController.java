package com.sweep.jaksim31.controller;


import com.sweep.jaksim31.service.KakaoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@RestController
public class KakaoController {
    KakaoService kakaoApi = new KakaoService();

    @RequestMapping(value="/kakaologin")
    public ModelAndView login(@RequestParam("code") String code, HttpSession session) {
        // 카카오 인증코드로 토큰 얻기
        String accessToken = kakaoApi.getAccessToken(code);
        // 받은 토큰으로 유저 정보 얻기
        HashMap<String, Object> userInfo = kakaoApi.getUserInfo(accessToken);

        System.out.println("login info : " + userInfo.toString());
        if(userInfo.get("email") != null) {
            session.setAttribute("userId", userInfo.get("email"));
            session.setAttribute("accessToken", accessToken);
        }

        // Redirect를 위한 Redirectview 생성과 넘겨줄 파라미터 키-값 추가, 파라미터 값들 노출 여부 설정
        RedirectView redirectView = new RedirectView();
        redirectView.setExposeModelAttributes(false);
        redirectView.addStaticAttribute("userId", userInfo.get("email"));
        redirectView.addStaticAttribute("test", 1234);
        redirectView.setUrl("http://localhost:3000/home/landing");


        ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);
        return mav;
    }

    // 카카오 로그아웃
    @RequestMapping(value="/logout")
    public ModelAndView logout(HttpSession session) {
        ModelAndView mav = new ModelAndView();

        kakaoApi.kakaoLogout((String)session.getAttribute("accessToken"));
        session.removeAttribute("accessToken");
        session.removeAttribute("userId");
        mav.setViewName("index");
        return mav;
    }

}