package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.dto.login.LoginReqDTO;
import com.sweep.jaksim31.dto.member.MemberEmailDto;
import com.sweep.jaksim31.dto.member.MemberReqDTO;
import com.sweep.jaksim31.dto.member.MemberRespDTO;
import com.sweep.jaksim31.dto.member.MemberUpdateDTO;
import com.sweep.jaksim31.dto.token.TokenDTO;
import com.sweep.jaksim31.dto.token.TokenReqDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * AuthController 설명 : auth controller
 * @author jowonjun
 * @version 1.0.0
 * 작성일 : 2022/02/14
 **/
@Slf4j
@RestController
@RequestMapping("/v0/auth")
@RequiredArgsConstructor
public class AuthApisController {



//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(
//            HttpServletRequest request) {
//
//        return authService.logout(request);
//    }

    //로그아웃 만들기

//    @PostMapping("/login/kakao")
//    public ResponseEntity<?> createAuthenticationTokenByKakao(@RequestBody SocialLoginDto socialLoginDto) throws Exception {
//        //api 인증을 통해 얻어온 code값 받아오기
//        String username = authService.kakaoLogin(socialLoginDto.getToken());
//        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//        final String token = jwtTokenUtil.generateToken(userDetails);
//        return ResponseEntity.ok(new JwtResponse(token, userDetails.getUsername()));
//    }


}
