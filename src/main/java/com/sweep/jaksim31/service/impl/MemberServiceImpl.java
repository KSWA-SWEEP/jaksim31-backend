package com.sweep.jaksim31.service.impl;

import com.sweep.jaksim31.adapter.cache.RefreshTokenCacheAdapter;
import com.sweep.jaksim31.auth.CustomLoginIdPasswordAuthToken;
import com.sweep.jaksim31.auth.CustomUserDetailsService;
import com.sweep.jaksim31.auth.TokenProvider;
import com.sweep.jaksim31.domain.diary.Diary;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.domain.token.RefreshToken;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.enums.JwtExceptionType;
import com.sweep.jaksim31.enums.MemberExceptionType;
import com.sweep.jaksim31.enums.SuccessResponseType;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.service.MemberService;
import com.sweep.jaksim31.utils.CookieUtil;
import com.sweep.jaksim31.utils.RedirectionUtil;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

/**
 * packageName :  com.sweep.jaksim31.service.impl
 * fileName : MemberServiceImpl
 * author :  방근호
 * date : 2023-01-09
 * description : 사용자 등록 및 조회를 위한 Services
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현          Members 수정으로 인한 Service 세부 수정
 * 2023-01-12           방근호          회원가입 시 오브젝트 디렉토리 생성
 * 2023-01-15           방근호          MemberSaveRequest 수정으로 인한 toMember 요청 인자 변경
 * 2023-01-16           김주현          로그인 시 오늘 일기 id Set-Cookie
 * 2023-01-16           방근호          비밀번호 재설정 변경
 * 2023-01-17           방근호          비밀번호 재설정 메소드 이름 변경
 * 2023-01-17           방근호          로그인 로직 수정
 * 2023-01-18           김주현          id data type 변경(ObjectId -> String)
 * 2023-01-18           방근호          GetMyInfoByLoginId 리턴값 수정
 * 2023-01-23           방근호          ResponseEntity Wrapper class 제거
 * 2023-01-25           방근호          getMyInfoByLoginId 수정
 * 2023-01-25           방근호          getMyInfoByLoginId 제거
 * 2023-01-27           김주현          로그인/로그아웃 시 userId 쿠키 설정 및 refresh token은 addSecureCookie로 전달
 * 2023-01-30           방근호          인증 로직 변경으로 인한 쿠기 설정 추가
 * 2023-01-31           방근호,김주현    로그아웃 시 Cookie 삭제
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    @Value("${jwt.refresh-token-expire-time}")
    private long rtkLive;
    @Value("${jwt.access-token-expire-time}")
    private long accExpTime;
    private final DiaryRepository diaryRepository;
    private final RedirectionUtil redirectionUtil;
    private final RefreshTokenCacheAdapter refreshTokenCacheAdapter;


    @Transactional
    public String signup(MemberSaveRequest memberRequestDto) {

        if(memberRepository.existsByLoginId(memberRequestDto.getLoginId()))
           throw new BizException(MemberExceptionType.DUPLICATE_USER);

        Members members = memberRequestDto.toMember(passwordEncoder, false);
        memberRepository.save(members);
        return SuccessResponseType.SIGNUP_SUCCESS.getMessage();
    }
    @Override
    @Transactional
    public String login(LoginRequest loginRequest, HttpServletResponse response) {

        // 회원 인증
        CustomLoginIdPasswordAuthToken customLoginIdPasswordAuthToken = new CustomLoginIdPasswordAuthToken(loginRequest.getLoginId(), loginRequest.getPassword());
        Authentication authenticate = authenticationManager.authenticate(customLoginIdPasswordAuthToken);
        String loginId = authenticate.getName();
        Members members = customUserDetailsService.getMember(loginId);
        if(members.getDelYn()=='Y')
            throw new BizException(MemberExceptionType.DELETED_USER);

        // 토큰 생성
        String accessToken = tokenProvider.createAccessToken(loginId, members.getAuthorities());
        String refreshToken = tokenProvider.createRefreshToken(loginId, members.getAuthorities());

        // 쿠키에 토큰 정보 및 인증 정보 저장
        CookieUtil.addSecureCookie(response, "atk", accessToken, (int) rtkLive / 60);
        CookieUtil.addSecureCookie(response, "rtk", refreshToken, (int) rtkLive / 60);
        CookieUtil.addPublicCookie(response, "isLogin", "true");
        CookieUtil.addPublicCookie(response, "userId", members.getId());
        CookieUtil.addPublicCookie(response, "isSocial", members.getIsSocial().toString());

        // 레디스에 캐싱
        refreshTokenCacheAdapter.put(loginId, refreshToken, Duration.ofSeconds((int) rtkLive / 60));

        LocalDate today = LocalDate.now();
        Diary todayDiary = diaryRepository.findDiaryByUserIdAndDate(members.getId(), today.atTime(9,0)).orElse(null);
        // 만료 시간을 당일 23:59:59로 설정
        long todayExpTime = LocalDateTime.of(today.plusDays(1), LocalTime.of(23, 59, 59,59)).toLocalTime().toSecondOfDay()
                - LocalDateTime.now().toLocalTime().toSecondOfDay() + ((long)3600*9); // GMT로 설정되어서 3600*9 추가..

        CookieUtil.addCookie(response, "todayDiaryId", Objects.nonNull(todayDiary) ? todayDiary.getId() : "", todayExpTime);

        return SuccessResponseType.LOGIN_SUCCESS.getMessage();

    }

    @Generated
    public HttpServletResponse reissue(HttpServletRequest request,
                                     HttpServletResponse response) {
        // method excluded form coverage report

        // cookie에서 refresh token 추출
        Cookie refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(req -> req.getName().equals("rtk"))
                .findAny()
                .orElseThrow(() -> new BizException(JwtExceptionType.EMPTY_TOKEN));

        String originRefreshToken = refreshTokenCookie.getValue();
        // refreshToken 검증
        int refreshTokenFlag = tokenProvider.validateToken(originRefreshToken);
        log.debug("refreshTokenFlag = {}", refreshTokenFlag);

        //refreshToken 검증하고 상황에 맞는 오류를 내보낸다.
        if (refreshTokenFlag == -1) {
            throw new BizException(JwtExceptionType.BAD_TOKEN); // 잘못된 리프레시 토큰
        } else if (refreshTokenFlag == 2) {
            throw new BizException(JwtExceptionType.REFRESH_TOKEN_EXPIRED); // 유효기간 끝난 토큰
        }

        // 2. Access Token 에서 Member LoginId 가져오기
        Authentication authentication = tokenProvider.getAuthentication(originRefreshToken);
        log.debug("Authentication = {}", authentication);

        // 캐시에 리프레시 토큰이 있는지 확인
        RefreshToken cachedRefreshToken = RefreshToken.builder()
                .loginId(authentication.getName())
                .value(refreshTokenCacheAdapter.get(authentication.getName()))
                .build();


        // 캐시에 리프레시 토큰이 없는 경우 로그아웃 처리
        if (Objects.isNull(cachedRefreshToken.getValue())) {
            throw new BizException(MemberExceptionType.LOGOUT_MEMBER);
        } else {
            if (!cachedRefreshToken.getValue().equals(originRefreshToken)) {
                throw new BizException(JwtExceptionType.BAD_TOKEN);
            }
        }

        // 5. 새로운 토큰 생성
        String loginId = tokenProvider.getMemberLoginIdByToken(originRefreshToken);
        Members members = customUserDetailsService.getMember(loginId);
        String newAccessToken = tokenProvider.createAccessToken(loginId, members.getAuthorities());
        String newRefreshToken = tokenProvider.createRefreshToken(loginId, members.getAuthorities());

        // 저장소 정보 업데이트
        refreshTokenCacheAdapter.put(loginId, newRefreshToken, Duration.ofSeconds(rtkLive / 60));

        log.debug("refresh Origin = {}", originRefreshToken);
        log.debug("refresh New = {} ", newRefreshToken);

        // 로그인 여부 및 토큰 만료 시간 Cookie 설정
        CookieUtil.addSecureCookie(response, "atk", newAccessToken, (int) rtkLive / 60);
        CookieUtil.addSecureCookie(response, "rtk", newRefreshToken, (int) rtkLive / 60);
        CookieUtil.addPublicCookie(response, "isLogin", "true");

        // 토큰 발급
        return response;
    }
    @Override
    @Transactional
    public String logout(HttpServletRequest request, HttpServletResponse response){

        Cookie refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(req -> req.getName().equals("atk"))
                .findAny()
                .orElseThrow(() -> new BizException(JwtExceptionType.EMPTY_TOKEN));

        String originAccessToken = refreshTokenCookie.getValue();

        // 쿠키에서 토큰 삭제 작업
        CookieUtil.resetDefaultCookies(response);

        Authentication authentication = tokenProvider.getAuthentication(originAccessToken);

        // 저장소에서 토큰 삭제
        refreshTokenCacheAdapter.delete(authentication.getName());

        return SuccessResponseType.LOGOUT_SUCCESS.getMessage();
    }

    @Transactional
    public String isMember(MemberCheckLoginIdRequest memberRequestDto) {

        if (!memberRepository.existsByLoginId(memberRequestDto.getLoginId()))
            throw new BizException(MemberExceptionType.NOT_FOUND_USER);

        return memberRequestDto.getLoginId() + SuccessResponseType.IS_MEMBER_SUCCESS.getMessage();
    }

    @Transactional
    public String updatePassword(String loginId, MemberUpdatePasswordRequest dto) {
        Members members = memberRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        members.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        // 업데이트 한 정보 저장
        memberRepository.save(members);
        return SuccessResponseType.USER_UPDATE_SUCCESS.getMessage();
    }


    /**
     *
     * @param userId 회원 아이디
     * @return MemberInfoResponse
     */
    @Cacheable(
            value = "memberCache",
            key = "#userId"
    )
    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(String userId, HttpServletRequest request) {
        MemberInfoResponse members = memberRepository.findById(userId)
                .map(MemberInfoResponse::of)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
        // 토큰의 id와 조회하려고 하는 id가 일치하지 않는 경우
        if(!tokenProvider.getMemberLoginIdByToken(CookieUtil.getAccessToken(request)).equals(members.getLoginId()))
            throw new BizException(MemberExceptionType.NO_PERMISSION);

        return members;
    }

    /**
     * DirtyChecking 을 통한 멤버 업데이트 ( Login ID는 업데이트 할 수 없다.)
     * @param userId
     * @param memberUpdateRequest member 수정 요청 dto
     */

    @CacheEvict(
            value = "memberCache",
            key = "#userId"
    )
    @Transactional
    public String updateMemberInfo(String userId, MemberUpdateRequest memberUpdateRequest, HttpServletRequest request) {
        Members members = memberRepository
                .findById(userId)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
        // 토큰의 id와 정보를 변경하려고 하는 id가 일치하지 않는 경우
        if(!tokenProvider.getMemberLoginIdByToken(CookieUtil.getAccessToken(request)).equals(members.getLoginId()))
            throw new BizException(MemberExceptionType.NO_PERMISSION);

        members.updateMember( memberUpdateRequest);
        memberRepository.save(members);
        return SuccessResponseType.USER_UPDATE_SUCCESS.getMessage();
    }

    @Transactional
    public String isMyPassword(String loginId, MemberCheckPasswordRequest dto){
        Members members = memberRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), members.getPassword()))
            throw new BizException(MemberExceptionType.WRONG_PASSWORD);

        return SuccessResponseType.CHECK_PW_SUCCESS.getMessage();
    }

    @CacheEvict(
            value = "memberCache",
            key = "#userId"
    )
    @Transactional
    public String remove(String userId, MemberRemoveRequest dto, HttpServletResponse response, HttpServletRequest request) throws URISyntaxException {
        // 멤버가 없을 경우 200 리턴 (멱등성을 위해)
        Members members = memberRepository
                .findById(userId)
                .orElseThrow(() -> new BizException(MemberExceptionType.DELETE_NOT_FOUND_USER, redirectionUtil.getHomeUrl()));

        // 토큰의 id와 정보를 변경하려고 하는 id가 일치하지 않는 경우
        if(!tokenProvider.getMemberLoginIdByToken(CookieUtil.getAccessToken(request)).equals(members.getLoginId()))
            throw new BizException(MemberExceptionType.NO_PERMISSION);

        // 비밀번호가 불일치 할 경우
        if (!passwordEncoder.matches(dto.getPassword(), members.getPassword())) {
            throw new BizException(MemberExceptionType.WRONG_PASSWORD);
        }

        // 멤버 엔티티의 delYn을 Yes로 변경 후 삭제 처리
        members.remove('Y');
        memberRepository.save(members);

        // 저장소에서 토큰 삭제
        refreshTokenCacheAdapter.delete(dto.getUserId());
        // 쿠키 삭제
        CookieUtil.resetDefaultCookies(response);

        return SuccessResponseType.USER_REMOVE_SUCCESS.getMessage();
    }
}
