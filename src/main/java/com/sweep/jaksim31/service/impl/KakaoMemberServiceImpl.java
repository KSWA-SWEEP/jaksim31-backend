package com.sweep.jaksim31.service.impl;

import com.sweep.jaksim31.adapter.cache.RefreshTokenCacheAdapter;
import com.sweep.jaksim31.auth.CustomLoginIdPasswordAuthToken;
import com.sweep.jaksim31.auth.CustomUserDetailsService;
import com.sweep.jaksim31.auth.TokenProvider;
import com.sweep.jaksim31.controller.feign.KakaoOAuthInfoFeign;
import com.sweep.jaksim31.controller.feign.KakaoOAuthLogoutFeign;
import com.sweep.jaksim31.controller.feign.KakaoOAuthTokenFeign;
import com.sweep.jaksim31.domain.diary.Diary;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.dto.login.KakaoOAuth;
import com.sweep.jaksim31.dto.login.KakaoProfile;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.domain.token.RefreshTokenRepository;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.MemberSaveRequest;
import com.sweep.jaksim31.enums.MemberExceptionType;
import com.sweep.jaksim31.enums.SuccessResponseType;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.enums.JwtExceptionType;
import com.sweep.jaksim31.service.MemberService;
import com.sweep.jaksim31.utils.CookieUtil;
import com.sweep.jaksim31.utils.RedirectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * packageName :  com.sweep.jaksim31.service.impl
 * fileName : KaKaoServiceImpl
 * author :  ??????
 * date : 2023-01-11
 * description : ?????? ????????? ?????? ?????? 403 ??????
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-11            ??????                ?????? ??????
 * 2023-01-12            ??????        Kakao ????????? /???????????? ?????? ??????
 * 2023-01-13            ??????                ?????? ?????? ??????
 * 2023-01-15            ?????????            ????????????/????????? ?????? ??? ????????????
 * 2023-01-30           ?????????             ?????? ?????? ???????????? ?????? ?????? ?????? ??????
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMemberServiceImpl implements MemberService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final KakaoOAuthTokenFeign kakaoOAuthTokenFeign;
    private final KakaoOAuthInfoFeign kakaoOAuthInfoFeign;
    private final KakaoOAuthLogoutFeign kakaoOAuthLogoutFeign;
    private final RedirectionUtil redirectionUtil;
    private final DiaryRepository diaryRepository;
    @Value("${jwt.refresh-token-expire-time}")
    private long rtkLive;
    @Value("${jwt.access-token-expire-time}")
    private long atkLive;

    private final RefreshTokenCacheAdapter refreshTokenCacheAdapter;



    @Override
    @Transactional
    public String login(LoginRequest loginRequest, HttpServletResponse response) throws URISyntaxException {

        // ????????? ?????? ?????? ?????? ??????
        if (!memberRepository.existsByLoginId(loginRequest.getLoginId())) {
            MemberSaveRequest memberSaveRequest = loginRequest.toMemberSaveRequest();
            Members members = memberSaveRequest.toMember(passwordEncoder, true);
            log.info(members.toString());
            memberRepository.save(members);
        }

        log.debug(loginRequest.getLoginId() +" " +  loginRequest.getPassword());

        CustomLoginIdPasswordAuthToken customLoginIdPasswordAuthToken =
                new CustomLoginIdPasswordAuthToken(loginRequest.getLoginId(), loginRequest.getPassword());

        Authentication authenticate = authenticationManager.authenticate(customLoginIdPasswordAuthToken);
        String loginId = authenticate.getName();
        Members members = customUserDetailsService.getMember(loginId);

        String accessToken = tokenProvider.createAccessToken(loginId, members.getAuthorities());
        String refreshToken = tokenProvider.createRefreshToken(loginId, members.getAuthorities());

        // ?????? ??????
        CookieUtil.addSecureCookie(response, "rtk", refreshToken, (int) rtkLive / 60);
        CookieUtil.addSecureCookie(response, "atk", accessToken, (int) rtkLive / 60);
        CookieUtil.addPublicCookie(response, "isLogin", "true");
        CookieUtil.addPublicCookie(response, "userId", members.getId());
        CookieUtil.addPublicCookie(response, "isSocial", members.getIsSocial().toString());

        // ????????? ?????? ????????????
        refreshTokenCacheAdapter.put(authenticate.getName(), refreshToken, Duration.ofSeconds(rtkLive / 60));

        LocalDate today = LocalDate.now();
        Diary todayDiary = diaryRepository.findDiaryByUserIdAndDate(members.getId(), today.atTime(9,0)).orElse(null);
        // ?????? ????????? ?????? 23:59:59??? ??????
        long todayExpTime = LocalDateTime.of(today.plusDays(1), LocalTime.of(23, 59, 59,59)).toLocalTime().toSecondOfDay()
                - LocalDateTime.now().toLocalTime().toSecondOfDay() + ((long)3600*9); // GMT??? ??????????????? 3600*9 ??????..

        CookieUtil.addCookie(response, "todayDiaryId", Objects.nonNull(todayDiary) ? todayDiary.getId() : "", todayExpTime);

        return SuccessResponseType.KAKAO_LOGIN_SUCCESS.getMessage();

    }

    @Override
    @Transactional
    public String logout(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {

        Cookie refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(req -> req.getName().equals("atk"))
                .findAny()
                .orElseThrow(() -> new BizException(JwtExceptionType.EMPTY_TOKEN));

        String originAccessToken = refreshTokenCookie.getValue();

        Authentication authentication = tokenProvider.getAuthentication(originAccessToken);
        String loginId = authentication.getName();

        // ????????? ?????? ????????? ?????? ?????? ??????
        ResponseEntity<String> res = kakaoOAuthLogoutFeign.requestLogout(loginId);
        log.debug(res.getBody());
        log.debug(String.valueOf(res.getStatusCode()));

        // ????????? ?????? ?????? ?????? ??????
        // ???????????? ?????? ?????? ??????
        CookieUtil.resetDefaultCookies(response);

        // ??????????????? ?????? ??????
        refreshTokenCacheAdapter.delete(authentication.getName());

        return SuccessResponseType.KAKAO_LOGOUT_SUCCESS.getMessage();
    }

        // ????????? ??????????????? ?????? Access Token ???????????? ??????
    public String getAccessToken (String code){
        ResponseEntity<KakaoOAuth> res = kakaoOAuthTokenFeign.getAccessToken(code);
        log.info(String.valueOf(res.getStatusCode()));
        log.info(String.valueOf(res.getBody()));

        return "Bearer " + Objects.requireNonNull(res.getBody()).getAccessToken();
    }

    // ????????? ??????????????? ?????? Access Token?????? ?????? ????????? ???????????? ??????
    public KakaoProfile getKakaoUserInfo (String accessToken){
        log.info("???????????? ????????????");
        ResponseEntity<KakaoProfile> res = kakaoOAuthInfoFeign.getUserInfo(accessToken);
        log.info(String.valueOf(res.getStatusCode()));
        log.info(String.valueOf(res.getBody()));

        if (!res.getStatusCode().equals(HttpStatus.OK)) {
            throw new BizException(MemberExceptionType.FAILED_KAKAO_OAUTH);
        }
        return res.getBody();
    }
}
