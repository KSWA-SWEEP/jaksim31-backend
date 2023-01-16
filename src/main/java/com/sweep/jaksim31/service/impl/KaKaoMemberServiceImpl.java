package com.sweep.jaksim31.service.impl;

import com.sweep.jaksim31.auth.CustomLoginIdPasswordAuthToken;
import com.sweep.jaksim31.auth.CustomUserDetailsService;
import com.sweep.jaksim31.auth.TokenProvider;
import com.sweep.jaksim31.controller.feign.KakaoOAuthInfoFeign;
import com.sweep.jaksim31.controller.feign.KakaoOAuthLogoutFeign;
import com.sweep.jaksim31.controller.feign.KakaoOAuthTokenFeign;
import com.sweep.jaksim31.dto.login.KakaoOAuth;
import com.sweep.jaksim31.dto.login.KakaoProfile;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.domain.token.RefreshToken;
import com.sweep.jaksim31.domain.token.RefreshTokenRepository;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.MemberSaveRequest;
import com.sweep.jaksim31.dto.token.TokenResponse;
import com.sweep.jaksim31.service.MemberService;
import com.sweep.jaksim31.utils.CookieUtil;
import com.sweep.jaksim31.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * packageName :  com.sweep.jaksim31.service.impl
 * fileName : KaKaoServiceImpl
 * author :  장건
 * date : 2023-01-11
 * description : 접근 권한이 없을 경우 403 응답
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-11            장건                최초 생성
 * 2023-01-12            장건        Kakao 로그인 /회원가입 연동 완료
 * 2023-01-13            장건                주석 정리 완료
 * 2023-01-15            방근호            회원가입/로그인 통합 및 리팩토링
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class KaKaoMemberServiceImpl implements MemberService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final KakaoOAuthTokenFeign kakaoOAuthTokenFeign;
    private final KakaoOAuthInfoFeign kakaoOAuthInfoFeign;
    private final KakaoOAuthLogoutFeign kakaoOAuthLogoutFeign;
    @Value("${jwt.refresh-token-expire-time}")
    private long rtkLive;

    @Value("${jwt.access-token-expire-time}")
    private long atkLive;
    @Value("${jwt.access-token-expire-time}")
    private long accExpTime;

    @Value("${kakao.auth.login-redirect-url}")
    private String loginRedirectUrl;

    @Value("${kakao.auth.logout-redirect-url}")
    private String logoutRedirectUrl;

    @Override
    @Transactional
    public ResponseEntity<TokenResponse> login(LoginRequest loginRequest, HttpServletResponse response) throws URISyntaxException {

        // 회원이 아닐 경우 회원 생성
        if (!memberRepository.existsByLoginId(loginRequest.getLoginId())) {
            MemberSaveRequest memberSaveRequest = loginRequest.toMemberSaveRequest();
            Members members = memberSaveRequest.toMember(passwordEncoder, true);
            System.out.println(members.toString());
            memberRepository.save(members);
        }

        System.out.println(loginRequest.getLoginId() +" " +  loginRequest.getPassword());

        CustomLoginIdPasswordAuthToken customLoginIdPasswordAuthToken =
                new CustomLoginIdPasswordAuthToken(loginRequest.getLoginId(), loginRequest.getPassword());

        Authentication authenticate = authenticationManager.authenticate(customLoginIdPasswordAuthToken);
        String loginId = authenticate.getName();
        Members members = customUserDetailsService.getMember(loginId);

        String accessToken = tokenProvider.createAccessToken(loginId, members.getAuthorities());
        String refreshToken = tokenProvider.createRefreshToken(loginId, members.getAuthorities());


        int cookieMaxAge = (int) rtkLive / 60;

        CookieUtil.addCookie(response, "refresh_token", refreshToken, cookieMaxAge);
        // 추후 설정시간 변경 해야 함.
        // Redirection 시 데이터를 넘겨줄 수 없기 때문에, 쿠키 또는 헤더로 전달 해야 함.
        CookieUtil.addCookie(response, "access_token", accessToken, cookieMaxAge);

        // 로그인 여부 및 토큰 만료 시간 Cookie 설정
        String isLogin = "true";
        Date newExpTime = new Date(System.currentTimeMillis() + accExpTime);
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String expTime = sdf.format(newExpTime);
        CookieUtil.addPublicCookie(response, "isLogin", isLogin, cookieMaxAge);
        CookieUtil.addPublicCookie(response, "expTime", expTime, cookieMaxAge);

        //db에 token 저장
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .loginId(loginId)
                        .value(refreshToken)
                        .build()
        );

        // Redirect 주소 설정
        URI redirectUri = new URI(loginRedirectUrl+loginRequest.getLoginId());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accessToken", accessToken);
        httpHeaders.setLocation(redirectUri);

        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);

    }

    @Override
    @Transactional
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {

        String originAccessToken = HeaderUtil.getAccessToken(request);
        Authentication authentication = tokenProvider.getAuthentication(originAccessToken);
        String loginId = authentication.getName();

        // 카카오 인증 서버에 토큰 만료 요청
        ResponseEntity<String> res = kakaoOAuthLogoutFeign.requestLogout(loginId);
        System.out.println(res.getBody());
        System.out.println(res.getStatusCode());

        // 쿠키에 있는 토큰 정보 삭제
        CookieUtil.addCookie(response, "refresh_token", "", 0);

        // 로그인 여부 및 토큰 만료 시간 Cookie 설정
        String isLogin = "false";
        String expTime = "expTime";
        CookieUtil.addPublicCookie(response, "isLogin", isLogin, 0);
        CookieUtil.addPublicCookie(response, "expTime", expTime, 0);

        URI redirectUri = new URI(logoutRedirectUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);

        try{
            if(refreshTokenRepository.findByLoginId(loginId).isPresent()){
                refreshTokenRepository.deleteByLoginId(loginId);
            }
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (NullPointerException e){
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.BAD_REQUEST);
        }
    }

        // 카카오 인증서버로 부터 Access Token 받아오는 함수
    public String getAccessToken (String code){
        ResponseEntity<KakaoOAuth> res = kakaoOAuthTokenFeign.getAccessToken(code);
        System.out.println(res.getStatusCode());
        System.out.println(res.getBody());

        return "Bearer " + Objects.requireNonNull(res.getBody()).getAccessToken();
    }

    // 카카오 인증서버로 부터 Access Token으로 유저 정보를 받아오는 함수
    public KakaoProfile getKakaoUserInfo (String accessToken){
        System.out.println("유저정보 받아오기");
        ResponseEntity<KakaoProfile> res = kakaoOAuthInfoFeign.getUserInfo(accessToken);
        System.out.println(res.getStatusCode());
        System.out.println(res.getBody());

        if (!res.getStatusCode().equals(HttpStatus.OK)) {
            throw new RuntimeException("카카오 유저 정보 불러오기 실패");
        }
        return res.getBody();
    }
}