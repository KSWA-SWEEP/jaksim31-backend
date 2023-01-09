package com.sweep.jaksim31.service.impl;

import com.sweep.jaksim31.auth.CustomUserDetailsService;
import com.sweep.jaksim31.entity.members.Members;
import com.sweep.jaksim31.entity.members.MemberRepository;
import com.sweep.jaksim31.entity.token.RefreshToken;
import com.sweep.jaksim31.entity.token.RefreshTokenRepository;
import com.sweep.jaksim31.service.AuthService;
import com.sweep.jaksim31.util.HeaderUtil;
import com.sweep.jaksim31.util.CookieUtil;
import com.sweep.jaksim31.dto.token.TokenDTO;
import com.sweep.jaksim31.dto.token.TokenReqDTO;
import com.sweep.jaksim31.dto.login.LoginReqDTO;
import com.sweep.jaksim31.dto.member.MemberEmailDto;
import com.sweep.jaksim31.dto.member.MemberReqDTO;
import com.sweep.jaksim31.dto.member.MemberRespDTO;
import com.sweep.jaksim31.util.exceptionhandler.BizException;
import com.sweep.jaksim31.util.exceptionhandler.JwtExceptionType;
import com.sweep.jaksim31.util.exceptionhandler.MemberExceptionType;
import com.sweep.jaksim31.auth.CustomEmailPasswordAuthToken;
import com.sweep.jaksim31.auth.TokenProvider;
import com.sweep.jaksim31.dto.member.MemberUpdateDTO;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;
    @Value("${jwt.refresh-token-expire-time}")
    private long rtkLive;

    @Value("${jwt.access-token-expire-time}")
    private long accExpTime;


    @Override
    @Transactional
    public MemberRespDTO signup(MemberReqDTO memberRequestDto) {
        if (memberRepository.existsByEmail(memberRequestDto.getEmail())) {
            throw new BizException(MemberExceptionType.DUPLICATE_USER);
        }

        Members members = memberRequestDto.toMember(passwordEncoder);
        log.debug("member = {}", members);
        return MemberRespDTO.of(memberRepository.save(members));
    }
    @Override
    @Transactional
    public TokenDTO login(LoginReqDTO loginReqDTO, HttpServletResponse response) {
        CustomEmailPasswordAuthToken customEmailPasswordAuthToken = new CustomEmailPasswordAuthToken(loginReqDTO.getEmail(),loginReqDTO.getPassword());

        Authentication authenticate = authenticationManager.authenticate(customEmailPasswordAuthToken);
        String email = authenticate.getName();
        Members members = customUserDetailsService.getMember(email);

        String accessToken = tokenProvider.createAccessToken(email, members.getAuthorities());
        String refreshToken = tokenProvider.createRefreshToken(email, members.getAuthorities());


        int cookieMaxAge = (int) rtkLive / 60;

//        CookieUtil.addCookie(response, "access_token", accessToken, cookieMaxAge);
        CookieUtil.addCookie(response, "refresh_token", refreshToken, cookieMaxAge);

        // 로그인 여부 및 토큰 만료 시간 Cookie 설정
        String isLogin = "true";
        Date newExpTime = new Date(System.currentTimeMillis() + accExpTime);
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String expTime = sdf.format(newExpTime);
        CookieUtil.addPublicCookie(response, "isLogin", isLogin, cookieMaxAge);
        CookieUtil.addPublicCookie(response, "expTime", expTime, cookieMaxAge);
//        System.out.println("redis " + redisService.getValues(email));

        System.out.println(accessToken);
        System.out.println(refreshToken);

        //db에 token 저장
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .email(email)
                        .value(refreshToken)
                        .build()
        );

        return tokenProvider.createTokenDTO(accessToken,refreshToken, expTime);

    }
    @Override
    @Transactional
    public TokenDTO reissue(TokenReqDTO tokenReqDTO,
                            HttpServletResponse response) {


        String originRefreshToken = tokenReqDTO.getRefreshToken();

        // refreshToken 검증
        int refreshTokenFlag = tokenProvider.validateToken(originRefreshToken);
        log.debug("refreshTokenFlag = {}", refreshTokenFlag);

        //refreshToken 검증하고 상황에 맞는 오류를 내보낸다.
        if (refreshTokenFlag == -1) {
            throw new BizException(JwtExceptionType.BAD_TOKEN); // 잘못된 리프레시 토큰
        } else if (refreshTokenFlag == 2) {
            throw new BizException(JwtExceptionType.REFRESH_TOKEN_EXPIRED); // 유효기간 끝난 토큰
        }

        // 2. Access Token 에서 Member Email 가져오기
        Authentication authentication = tokenProvider.getAuthentication(originRefreshToken);
//        Authentication authentication = tokenProvider.getAuthentication(originAccessToken);
        log.debug("Authentication = {}", authentication);


        // DB에서 Member Email 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BizException(MemberExceptionType.LOGOUT_MEMBER)); // 로그 아웃된 사용자

        // Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(originRefreshToken)) {
            throw new BizException(JwtExceptionType.BAD_TOKEN); // 토큰이 일치하지 않습니다.
        }



        Date newExpTime = new Date(System.currentTimeMillis() + accExpTime);
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String expTime = sdf.format(newExpTime);

        // 5. 새로운 토큰 생성
        String email = tokenProvider.getMemberEmailByToken(originRefreshToken);
        Members members = customUserDetailsService.getMember(email);

        String newAccessToken = tokenProvider.createAccessToken(email, members.getAuthorities());
        String newRefreshToken = tokenProvider.createRefreshToken(email, members.getAuthorities());
        TokenDTO tokenDto = tokenProvider.createTokenDTO(newAccessToken, newRefreshToken, expTime);

        log.debug("refresh Origin = {}", originRefreshToken);
        log.debug("refresh New = {} ", newRefreshToken);


        int cookieMaxAge = (int) rtkLive / 60;
        CookieUtil.addCookie(response, "refresh_token", newRefreshToken, cookieMaxAge);

        // 로그인 여부 및 토큰 만료 시간 Cookie 설정
        String isLogin = "true";
        CookieUtil.addPublicCookie(response, "isLogin", isLogin, cookieMaxAge);
        CookieUtil.addPublicCookie(response, "expTime", expTime, cookieMaxAge);
//
        // 6. 저장소 정보 업데이트 (dirtyChecking으로 업데이트)
        refreshToken.updateValue(newRefreshToken);

            // 토큰 발급
//        return ApiResponse.success("token", newAccessToken);
            return tokenDto;
        }
    @Override
    @Transactional
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){

        String originAccessToken = HeaderUtil.getAccessToken(request);

//        CookieUtil.deleteCookie(request, response, "access_token");
//        CookieUtil.deleteCookie(request, response, "refresh_token");
        String initValue = "";
//        CookieUtil.addCookie(response, "access_token", initValue,0);
        CookieUtil.addCookie(response, "refresh_token", initValue, 0);

        // 로그인 여부 및 토큰 만료 시간 Cookie 설정
        String isLogin = "false";
        String expTime = "expTime";
        CookieUtil.addPublicCookie(response, "isLogin", isLogin, 0);
        CookieUtil.addPublicCookie(response, "expTime", expTime, 0);

        Authentication authentication = tokenProvider.getAuthentication(originAccessToken);
        String email = authentication.getName();

        try{
            if(refreshTokenRepository.findByEmail(email).isPresent()){
                refreshTokenRepository.deleteByEmail(email);
            }
        } catch (NullPointerException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        refreshTokenRepository.deleteByEmail(email);

//        System.out.println(redisService.getValues(email));

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
    @Override
    @Transactional
    public Optional<Members> isMember(MemberEmailDto memberRequestDto) {
        if (memberRepository.existsByEmail(memberRequestDto.getEmail())) {
            return memberRepository.findByEmail(memberRequestDto.getEmail());
        }else
            return null;
    }
    @Override
    @Transactional
    public void updatePw(MemberUpdateDTO dto) {
        Members members = memberRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        members.updateMember(dto, passwordEncoder);
    }

}
