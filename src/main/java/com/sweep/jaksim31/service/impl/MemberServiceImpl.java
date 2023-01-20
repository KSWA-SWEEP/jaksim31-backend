package com.sweep.jaksim31.service.impl;

import com.sweep.jaksim31.auth.CustomLoginIdPasswordAuthToken;
import com.sweep.jaksim31.auth.CustomUserDetailsService;
import com.sweep.jaksim31.auth.TokenProvider;
import com.sweep.jaksim31.domain.diary.Diary;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.domain.token.RefreshToken;
import com.sweep.jaksim31.domain.token.RefreshTokenRepository;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.dto.token.TokenRequest;
import com.sweep.jaksim31.dto.token.TokenResponse;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.JwtExceptionType;
import com.sweep.jaksim31.exception.type.MemberExceptionType;
import com.sweep.jaksim31.service.MemberService;
import com.sweep.jaksim31.utils.CookieUtil;
import com.sweep.jaksim31.utils.HeaderUtil;
import com.sweep.jaksim31.utils.RedirectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

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
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;
    @Value("${jwt.refresh-token-expire-time}")
    private long rtkLive;
    @Value("${jwt.access-token-expire-time}")
    private long accExpTime;
    private final DiaryRepository diaryRepository;
    private final RedirectionUtil redirectionUtil;

//    @Value("${home.url}")
//    private String homeUrl;

    @Transactional
    public ResponseEntity<MemberSaveResponse> signup(MemberSaveRequest memberRequestDto) {

        if(memberRepository.existsByLoginId(memberRequestDto.getLoginId()))
           throw new BizException(MemberExceptionType.DUPLICATE_USER);

        Members members = memberRequestDto.toMember(passwordEncoder, false);
        return ResponseEntity.ok(MemberSaveResponse.of(memberRepository.save(members)));
    }
    @Override
    @Transactional
    public ResponseEntity<TokenResponse> login(LoginRequest loginRequest, HttpServletResponse response) {
        CustomLoginIdPasswordAuthToken customLoginIdPasswordAuthToken = new CustomLoginIdPasswordAuthToken(loginRequest.getLoginId(), loginRequest.getPassword());

        Authentication authenticate = authenticationManager.authenticate(customLoginIdPasswordAuthToken);
        String loginId = authenticate.getName();
        Members members = customUserDetailsService.getMember(loginId);

        String accessToken = tokenProvider.createAccessToken(loginId, members.getAuthorities());
        String refreshToken = tokenProvider.createRefreshToken(loginId, members.getAuthorities());

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
//        System.out.println("redis " + redisService.getValues(loginId));

        // db에 있을 경우 지워준다.
        if(refreshTokenRepository.findByLoginId(loginId).isPresent())
            refreshTokenRepository.deleteByLoginId(loginId);

        //db에 token 저장
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .loginId(loginId)
                        .value(refreshToken)
                        .build()
        );

        return ResponseEntity.ok(tokenProvider.createTokenDTO(accessToken,refreshToken, expTime,loginId));

    }

    @Transactional
    public ResponseEntity<TokenResponse> reissue(TokenRequest tokenRequest,
                                     HttpServletResponse response) {


        String originRefreshToken = tokenRequest.getRefreshToken();

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
//        Authentication authentication = tokenProvider.getAuthentication(originAccessToken);
        log.debug("Authentication = {}", authentication);


        // DB에서 Member LoginId 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByLoginId(authentication.getName())
                .orElseThrow(() -> new BizException(MemberExceptionType.LOGOUT_MEMBER)); // 로그 아웃된 사용자

        // Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(originRefreshToken)) {
            throw new BizException(JwtExceptionType.BAD_TOKEN);
        }

        Date newExpTime = new Date(System.currentTimeMillis() + accExpTime);
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String expTime = sdf.format(newExpTime);

        // 5. 새로운 토큰 생성
        String loginId = tokenProvider.getMemberLoginIdByToken(originRefreshToken);
        Members members = customUserDetailsService.getMember(loginId);

        String newAccessToken = tokenProvider.createAccessToken(loginId, members.getAuthorities());
        String newRefreshToken = tokenProvider.createRefreshToken(loginId, members.getAuthorities());
        TokenResponse tokenResponse = tokenProvider.createTokenDTO(newAccessToken, newRefreshToken, expTime, loginId);

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
        return ResponseEntity.ok(tokenResponse);
    }
    @Override
    @Transactional
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){

        String originAccessToken = HeaderUtil.getAccessToken(request);

        // 쿠키에서 삭제 작업
        String initValue = "";
        CookieUtil.addCookie(response, "refresh_token", initValue, 0);

        // 로그인 여부 및 토큰 만료 시간 Cookie 설정
        String isLogin = "false";
        String expTime = "expTime";
        CookieUtil.addPublicCookie(response, "isLogin", isLogin, 0);
        CookieUtil.addPublicCookie(response, "expTime", expTime, 0);

        Authentication authentication = tokenProvider.getAuthentication(originAccessToken);
        String loginId = authentication.getName();

        refreshTokenRepository
                .findByLoginId(loginId)
                .orElseThrow(()->new BizException(JwtExceptionType.LOGOUT_EMPTY_TOKEN));

        refreshTokenRepository.deleteByLoginId(loginId);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @Transactional
    public ResponseEntity<String> isMember(MemberCheckLoginIdRequest memberRequestDto) {

        if (!memberRepository.existsByLoginId(memberRequestDto.getLoginId()))
            throw new BizException(MemberExceptionType.NOT_FOUND_USER);

        return ResponseEntity.ok(memberRequestDto.getLoginId() + " 해당 이메일은 가입하였습니다.");
    }

    @Transactional
    public ResponseEntity<String> updatePassword(String loginId, MemberUpdatePasswordRequest dto) {
        Members members = memberRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        members.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        // 업데이트 한 정보 저장
        memberRepository.save(members);
        return ResponseEntity.ok("회원 정보가 정상적으로 변경되었습니다.");
    }


    /**
     *
     * @param userId 회원 아이디
     * @return MemberInfoResponse
     */

    @Transactional(readOnly = true)
    public ResponseEntity<MemberInfoResponse> getMyInfo(String userId) {
        return ResponseEntity.ok().body(memberRepository.findById(userId)
                .map(MemberInfoResponse::of)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER)));
    }


    public ResponseEntity<MemberInfoResponse> getMyInfoByLoginId(String loginId) {
        // 로그인 id로 사용자 정보 불러오기
        Members member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
        // 오늘 날짜로 작성 된 일기가 있는지 확인
        LocalDate today = LocalDate.now();
        Diary todayDiary = diaryRepository.findDiaryByUserIdAndDate(member.getId(), today.atTime(9,0)).orElse(null);
        // 작성 된 일기가 있다면 diary_id, 없으면 ""
        String todayDiaryId = "";
        if(todayDiary != null)
            todayDiaryId = todayDiary.getId().toString();
        // 만료 시간을 당일 23:59:59로 설정
        long expTime = LocalTime.of(23,59,59).toSecondOfDay() - LocalTime.now().minusHours(9).toSecondOfDay();
        // check
//        System.out.println("### end of day : " + LocalTime.of(23,59,59) + "    " + LocalTime.of(23,59,59).toSecondOfDay());
//        System.out.println("### now : " + LocalTime.now().toString() + "   " + LocalTime.now().toSecondOfDay());
//        System.out.println("### exptime : " + expTime);

        // todayDiaryId Cookie 설정
        ResponseCookie responseCookie = ResponseCookie.from("todayDiaryId", todayDiaryId)
                .httpOnly(true)
                .secure(true)
                .maxAge(expTime)
                .path("/").build();

        // 응답 생성(Header(쿠키 설정) + Body(사용자 정보))
        return ResponseEntity.ok().header("Set-Cookie", responseCookie.toString())
                        .body(Optional.of(member)
                        .map(MemberInfoResponse::of)
                        .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER)));
    }


    /**
     * DirtyChecking 을 통한 멤버 업데이트 ( Login ID는 업데이트 할 수 없다.)
     * @param userId
     * @param memberUpdateRequest member 수정 요청 dto
     */

    @Transactional
    public ResponseEntity<String> updateMemberInfo(String userId, MemberUpdateRequest memberUpdateRequest) {
        Members members = memberRepository
                .findById(userId)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        members.updateMember( memberUpdateRequest);
        memberRepository.save(members);
        return ResponseEntity.ok("회원 정보가 정상적으로 변경되었습니다.");
    }

    @Transactional
    public ResponseEntity<String> isMyPassword(String loginId, MemberCheckPasswordRequest dto){
        Members members = memberRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), members.getPassword()))
            throw new BizException(MemberExceptionType.WRONG_PASSWORD);

        return ResponseEntity.ok("비밀번호가 일치합니다.");
    }

    @Transactional
    public ResponseEntity<String> remove(String userId, MemberRemoveRequest dto) throws URISyntaxException {
        // 멤버가 없을 경우 200 리턴 (멱등성을 위해)
        Members entity = memberRepository
                .findById(userId)
                .orElseThrow(() -> new BizException(MemberExceptionType.DELETE_NOT_FOUND_USER, redirectionUtil.getHomeUrl()));

        // 비밀번호가 불일치 할 경우
        if (!passwordEncoder.matches(entity.getPassword(), dto.getPassword()))
            throw new BizException(MemberExceptionType.WRONG_PASSWORD);

        // 멤버 엔티티의 delYn을 Yes로 변경 후 삭제 처리
        entity.remove('Y');
        memberRepository.save(entity);

        return new ResponseEntity<>("정상적으로 회원탈퇴 작업이 처리되었습니다.", redirectionUtil.getLocationHeader(), HttpStatus.SEE_OTHER);
    }
}
