package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.dto.login.KaKaoInfoDTO;
import com.sweep.jaksim31.dto.login.LoginReqDTO;
import com.sweep.jaksim31.entity.members.MemberRepository;
import com.sweep.jaksim31.service.impl.KaKaoServiceImpl;
import com.sweep.jaksim31.service.impl.MemberServiceImpl;
import com.sweep.jaksim31.util.exceptionhandler.BizException;
import com.sweep.jaksim31.util.exceptionhandler.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoController {
    private final KaKaoServiceImpl kakaoApi;
    private final MemberServiceImpl MemberServiceImpl;
    private final MemberRepository memberRepository;
    @RequestMapping(value="v0/auth/kakaologin")
    public ModelAndView kakaologin(@RequestParam("code") String code, @RequestBody KaKaoInfoDTO loginReqDTO, HttpSession session, HttpServletResponse response) {
        // 카카오 인증코드로 토큰 얻기
        String kakaoApiAccessToken = kakaoApi.getAccessToken(code);
        // 받은 토큰으로 유저 정보 얻기
        KaKaoInfoDTO userInfo = kakaoApi.getUserInfo(kakaoApiAccessToken);
        loginReqDTO = userInfo;

        if(userInfo.getLoginId() != null) {
            session.setAttribute("userId", userInfo.getUserName());
            session.setAttribute("accessToken", kakaoApiAccessToken);
        }


        // 회원가입이 되어있는지 조회하고 없으면 회원가입 있으면 로그인.
        if (memberRepository.existsByLoginId(userInfo.getLoginId())) {
            kakaoApi.kakaologin(loginReqDTO, response);
            System.out.println("Existing ID");
        }
        else {
            kakaoApi.kakaosignup(userInfo);
        }

         // Redirect를 위한 Redirectview 생성과 넘겨줄 파라미터 키-값 추가, 파라미터 값들 노출 여부 설정
        RedirectView redirectView = new RedirectView();
        redirectView.setExposeModelAttributes(false);
        redirectView.addStaticAttribute("userId", userInfo.getLoginId());
        //redirectView.addStaticAttribute("nickname", userInfo.get("nickname"));
        redirectView.setUrl("http://localhost:3000/home/landing");


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