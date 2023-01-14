package com.sweep.jaksim31.service.impl;

import com.sweep.jaksim31.auth.CustomLoginIdPasswordAuthToken;
import com.sweep.jaksim31.auth.CustomUserDetailsService;
import com.sweep.jaksim31.auth.TokenProvider;
import com.sweep.jaksim31.controller.feign.ApiTokenRefreshFeign;
import com.sweep.jaksim31.controller.feign.MakeObjectDirectoryFeign;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.dto.token.TokenResponse;
import com.sweep.jaksim31.dto.token.TokenRequest;
import com.sweep.jaksim31.domain.token.RefreshToken;
import com.sweep.jaksim31.domain.token.RefreshTokenRepository;
import com.sweep.jaksim31.service.MemberService;
import com.sweep.jaksim31.utils.CookieUtil;
import com.sweep.jaksim31.utils.HeaderUtil;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.JwtExceptionType;
import com.sweep.jaksim31.exception.type.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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
import java.util.Date;
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
    private final MakeObjectDirectoryFeign makeObjectDirectoryFeign;
    private final ApiTokenRefreshFeign apiTokenRefreshFeign;
    @Value("${jwt.refresh-token-expire-time}")
    private long rtkLive;

    @Value("${jwt.access-token-expire-time}")
    private long accExpTime;


    @Override
    @Transactional
    public ResponseEntity<MemberSaveResponse> signup(MemberSaveRequest memberRequestDto) {
        if (memberRepository.existsByLoginId(memberRequestDto.getLoginId())) {
            throw new BizException(MemberExceptionType.DUPLICATE_USER);
        }

        Members members = memberRequestDto.toMember(passwordEncoder);
        if(members.getPassword() == null)
            members.setIsSocial(true);
        log.debug("member = {}", members);

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

        //db에 token 저장
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .loginId(loginId)
                        .value(refreshToken)
                        .build()
        );

        return ResponseEntity.ok(tokenProvider.createTokenDTO(accessToken,refreshToken, expTime,loginId));

    }
    @Override
    @Transactional
    public ResponseEntity<?> reissue(TokenRequest tokenRequest,
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
            return new ResponseEntity<>("토큰이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
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
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){

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

        try{
            if(refreshTokenRepository.findByLoginId(loginId).isPresent()){
                refreshTokenRepository.deleteByLoginId(loginId);
            }
            return ResponseEntity.ok("로그아웃 되었습니다.");
        } catch (NullPointerException e){
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    @Transactional
    public ResponseEntity<?> isMember(MemberCheckLoginIdRequest memberRequestDto) {
        if (memberRepository.existsByLoginId(memberRequestDto.getLoginId())) {
            return ResponseEntity.ok(memberRequestDto.getLoginId() + " 해당 이메일은 가입하였습니다.");
        }else
            return ResponseEntity.notFound().build();
    }
    @Override
    @Transactional
    public ResponseEntity<?> updatePw(String id, MemberUpdateRequest dto) {
        Members members = memberRepository
                .findById(new ObjectId(id))
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
        try {
            members.updateMember(dto, passwordEncoder);
            // 업데이트 한 정보 저장
            memberRepository.save(members);
            return ResponseEntity.ok("회원 정보가 정상적으로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("비밀번호가 일치하지 않습니다.");
        }
    }


    /**
     * @return 요청한 ID의 유저 정보를 반환한다.
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<MemberInfoResponse> getMyInfo(String userId) {
        return ResponseEntity.ok().body(memberRepository.findById(new ObjectId(userId))
                .map(MemberInfoResponse::of)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER)));
    }

    @Override
    public ResponseEntity<MemberInfoResponse> getMyInfoByLoginId(String loginId) {
        return ResponseEntity.ok().body(memberRepository.findMembersByLoginId(loginId)
                .map(MemberInfoResponse::of)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER)));
    }


    /**
     * @param userId DirtyChecking 을 통한 멤버 업데이트 ( Login ID는 업데이트 할 수 없다.)
     * @param dto
     */
    @Override
    @Transactional
    public ResponseEntity<?> updateMemberInfo(String userId, MemberUpdateRequest dto) {
        Members members = memberRepository
                .findById(new ObjectId(userId))
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
        try {
            members.updateMember(dto, passwordEncoder);
            // 업데이트 한 정보 저장
            memberRepository.save(members);
            return ResponseEntity.ok("회원 정보가 정상적으로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Boolean> isMyPassword(String userId, MemberCheckPasswordRequest dto){
        Members members = memberRepository
                .findById(new ObjectId(userId))
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        if (passwordEncoder.matches(dto.getPassword(), members.getPassword())) return ResponseEntity.ok(true);
        else return ResponseEntity.notFound().build();
    }

    @Override
    @Transactional
    public ResponseEntity<String> remove(String userId, MemberRemoveRequest dto) {
        Members entity = memberRepository
                .findById(new ObjectId(userId))
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        entity.remove('Y');
        memberRepository.save(entity);
        return ResponseEntity.ok("정상적으로 회원탈퇴 작업이 처리되었습니다.");
    }

//    public ResponseEntity<?> makeObjectDirectory(String userId) throws BizException{
//        try {
//            ResponseEntity<Void> result = makeObjectDirectoryFeign.makeDir(userId);
//            if (!result.getStatusCode().equals(HttpStatus.CREATED)) {
//                throw new BizException(ObjectStorageExceptionType.NOT_CREATE_DIRECTORY);
//            }
//            return result;
//        } catch (Exception e) {
//            // 인증 API 토큰 발급 후 추출
//            ResponseEntity<String> tokenResponse = apiTokenRefreshFeign.refreshApiToken();
//            HttpHeaders responseHeaders = tokenResponse.getHeaders();
//            // 오브젝트 스토리지에 연결 요청 시 새로 받은 인증 API 토큰 적용R
//            MakeObjectDirectoryFeignConfig.authToken = Objects.requireNonNull(responseHeaders.get("x-subject-token")).get(0).toString();
//
//            // 재생성 요청
//            ResponseEntity<Void> result = makeObjectDirectoryFeign.makeDir(userId);
//
//            if (!result.getStatusCode().equals(HttpStatus.CREATED)) {
//                throw new BizException(ObjectStorageExceptionType.NOT_CREATE_DIRECTORY);
//            }
//            return result;
//        }
//    }

}
