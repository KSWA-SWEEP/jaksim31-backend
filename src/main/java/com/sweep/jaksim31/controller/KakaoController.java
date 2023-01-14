package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.dto.login.KakaoLoginRequest;
import com.sweep.jaksim31.service.impl.KaKaoServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * packageName :  com.sweep.jaksim31.dto/login
 * fileName : KaKaoController
 * author :  장건
 * date : 2023-01-11
 * description : 카카오 로그인/회원가입 한 멤버들을 우리 서비스로 연동시키기 위한 Controller
 * ======================================================================================
 * DATE                 AUTHOR                                NOTE
 * --------------------------------------------------------------------------------------
 * 2023-01-10            장건                                최초 생성
 * 2023-01-10            장건                KakaoToken 받기, 해당 토큰으로 User 정보 받아오기
 * 2023-01-13            장건                        Kakao 로그인/회원가입 서비스 연동
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoController {
    private final KaKaoServiceImpl kakaoApi;
    private final MemberRepository memberRepository;
    @RequestMapping(value="v0/auth/kakaologin")
    public ModelAndView kakaologin(@RequestParam("code") String code, HttpSession session, HttpServletResponse response) {
        // 카카오 인증코드로 토큰 얻기
        String kakaoApiAccessToken = kakaoApi.getAccessToken(code);

        // 받은 토큰으로 유저 정보 얻기
        KakaoLoginRequest userInfo = kakaoApi.getUserInfo(kakaoApiAccessToken);

        // HTTP 세션에 AccessToken, User Name 저장
        if(userInfo.getLoginId() != null) {
            session.setAttribute("userName", userInfo.getUsername());
            session.setAttribute("accessToken", kakaoApiAccessToken);
        }

        // Redirect를 위한 Redirectview 생성과 넘겨줄 파라미터 키-값 추가, 파라미터 값들 노출 여부 설정
        RedirectView redirectView = new RedirectView();
        redirectView.setExposeModelAttributes(false);
        redirectView.addStaticAttribute("nickname", userInfo.getUsername());

        System.out.println("CheckPoint1");
        // 회원가입이 되어있는지 조회하고 없으면 회원가입 있으면 로그인.
        if (memberRepository.existsByLoginId(userInfo.getLoginId())) {
            kakaoApi.kakaologin(userInfo, response);
            // Front 페이지로 Redirect
            redirectView.setUrl("http://localhost:3000/home/landing");
        }
        else {
            kakaoApi.kakaosignup(userInfo);
            // 추후 회원가입 성공 페이지로 Redirect
            redirectView.setUrl("http://localhost:3000/home/landing");
        }
        ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);
        return mav;
    }


//    // 카카오 로그아웃
//    @RequestMapping(value="/logout")
//    public ModelAndView logout(HttpSession session) {
//        ModelAndView mav = new ModelAndView();
//
//        kakaoApi.kakaoLogout((String)session.getAttribute("accessToken"));
//        session.removeAttribute("accessToken");
//        session.removeAttribute("userId");
//        mav.setViewName("index");
//        return mav;
//    }

}