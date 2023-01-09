package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.dto.login.LoginReqDTO;
import com.sweep.jaksim31.dto.member.MemberEmailDto;
import com.sweep.jaksim31.dto.member.MemberReqDTO;
import com.sweep.jaksim31.dto.member.MemberRespDTO;
import com.sweep.jaksim31.dto.member.MemberUpdateDTO;
import com.sweep.jaksim31.dto.token.TokenDTO;
import com.sweep.jaksim31.dto.token.TokenReqDTO;
import com.sweep.jaksim31.entity.members.Members;
import com.sweep.jaksim31.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

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

    private final AuthServiceImpl authServiceImpl;
    @Value("${jwt.refresh-token-expire-time}")
    private long rtkLive;

    @GetMapping("/test")
    public String test(){
        return "OK";
    }

    @PostMapping("/signup")
    public MemberRespDTO signup(@RequestBody MemberReqDTO memberRequestDto) {
        log.debug("memberRequestDto = {}",memberRequestDto);
        return authServiceImpl.signup(memberRequestDto);
    }

    @PostMapping("/login")
    public TokenDTO login(
            @RequestBody LoginReqDTO loginReqDTO,
            HttpServletResponse response) {
        return authServiceImpl.login(loginReqDTO, response);
    }

    @PostMapping("/isMember")
    public Optional<Members> isMember(
            @RequestBody MemberEmailDto memberRequestDto) {
        return authServiceImpl.isMember(memberRequestDto);
    }

    @Hidden
    @Operation(summary = "비밀번호 변경", description = "비밀번호 재설정을 요청합니다.")
    @PutMapping("/changePw")
    public void changePw(@RequestBody MemberUpdateDTO dto) {
        authServiceImpl.updatePw(dto);
    }

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

    @PostMapping("/reissue")
    public TokenDTO reissue(@RequestBody TokenReqDTO tokenReqDTO,
                            HttpServletResponse response
    ) {
        return authServiceImpl.reissue(tokenReqDTO, response);
    }
}
