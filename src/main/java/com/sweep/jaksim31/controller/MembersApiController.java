package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.dto.login.KakaoProfile;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.login.validator.LoginRequestValidator;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.dto.member.validator.*;
import com.sweep.jaksim31.dto.token.TokenRequest;
import com.sweep.jaksim31.dto.token.TokenResponse;
import com.sweep.jaksim31.dto.token.validator.TokenRequestValidator;
import com.sweep.jaksim31.service.impl.KaKaoMemberServiceImpl;
import com.sweep.jaksim31.service.impl.MemberServiceImpl;
import com.sweep.jaksim31.utils.RedirectionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * packageName :  com.sweep.jaksim31.controller
 * fileName : MembersApiController
 * author :  방근호
 * date : 2023-01-09
 * description : 인증 관련 API Controller
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             사용자 정보 조회(by LoginId) API 추가
 * 2023-01-13           장건              카카오 로그인 추가
 * 2023-01-15           방근호             카카오 로그인 api 리팩토링 및 로그아웃 추가
 * 2023-01-25           방근호             getMyInfoByLoginId 제거
 */

/* TODO
    * 카카오 로그아웃 구현 및 테스트! (프론트 연결 시)
    * 유저 삭제 시 삭제 계속 된다 수정~
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "멤버", description = "멤버 관련 api 입니다.")
@RequestMapping("/v0/members")
public class MembersApiController {
    private final MemberServiceImpl memberServiceImpl;
    private final KaKaoMemberServiceImpl kaKaoMemberService;
    @Value("${kakao.auth.login-redirect-url}")
    private String loginRedirectUrl;

    @Value("${home.url}")
    private String logoutRedirectUrl;

    @InitBinder
    public void init(WebDataBinder binder) {
        binder.addValidators(new LoginRequestValidator(), new MemberUpdateRequestValidator(), new MemberUpdatePasswordRequestValidator(), new MemberCheckLoginIdRequestValidator()
                , new MemberRemoveRequestValidator(), new MemberSaveRequestValidator(), new MemberCheckPasswordRequestValidator(), new TokenRequestValidator());
    }

    @GetMapping("/test")
    public String test(){
        return "OK";
    }

    @Operation(summary = "회원가입", description = "")
    @PostMapping("/register")
    public ResponseEntity<MemberSaveResponse> signup(@Validated @RequestBody MemberSaveRequest memberRequestDto) {
        log.debug("memberRequestDto = {}",memberRequestDto);
        return new ResponseEntity<>(memberServiceImpl.signup(memberRequestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "로그인", description = "유저 정보를 통해 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Validated @RequestBody LoginRequest loginRequest,
            HttpServletResponse response, @RequestParam("redirectUri") String redirectUri) throws URISyntaxException {

        // Redirect 주소 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI(redirectUri));
        httpHeaders.set("Access-Control-Allow-Origin", "*");

        return new ResponseEntity<>(memberServiceImpl.login(loginRequest, response), httpHeaders, HttpStatus.SEE_OTHER);
    }

    @Operation(summary = "카카오 로그인", description = "카카오 OAUTH를 이용하여 로그인 합니다.")
    @GetMapping(value="/kakao-login")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestParam("code") String authorizationCode, HttpServletResponse response,
                                                    @RequestParam("redirectUri") String redirectUri) throws Exception {
        System.out.println(authorizationCode);
        // 카카오 인증코드로 토큰 얻어서 유저 정보 얻기
        KakaoProfile userInfo = kaKaoMemberService.getKakaoUserInfo((kaKaoMemberService.getAccessToken(authorizationCode)));

        // Redirect 주소 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI(redirectUri));

        // 회원가입이 되어있는지 조회하고 없으면 회원가입 있으면 로그인.
        return new ResponseEntity<>(kaKaoMemberService.login(userInfo.toLoginRequest(), response), httpHeaders, HttpStatus.SEE_OTHER);
    }

    @Operation(summary = "회원가입 여부 확인", description = "이메일을 통해 회원가입 여부를 확인합니다.")
    @PostMapping("")
    public ResponseEntity<String> isMember(
            @Validated  @RequestBody MemberCheckLoginIdRequest memberRequestDto) {
        return ResponseEntity.ok(memberServiceImpl.isMember(memberRequestDto));
    }

    @Operation(summary = "토큰 재발급", description = "리프레쉬 토큰으로 토큰을 재발급 합니다.")
    @PostMapping("/{userId}/reissue")
    public ResponseEntity<TokenResponse> reissue(@PathVariable("userId") String userId, @Validated @RequestBody TokenRequest tokenRequest,
                            HttpServletResponse response
    ) {
        return ResponseEntity.ok(memberServiceImpl.reissue(tokenRequest, response));
    }

//    @Hidden
    @Operation(summary = "비밀번호 변경", description = "비밀번호 재설정을 요청합니다.")
    @PutMapping("/{loginId}/password")
    public ResponseEntity<String> changePassword(@PathVariable("loginId") String loginId, @Validated @RequestBody MemberUpdatePasswordRequest dto) {
        return ResponseEntity.ok(memberServiceImpl.updatePassword(loginId, dto));
    }

    // ================================================== //

    @Operation(summary = "개별 정보 조회", description = "자신의 정보를 요청합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<MemberInfoResponse> getMyInfo(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(memberServiceImpl.getMyInfo(userId));
    }

    @Operation(summary = "유저 정보 업데이트 요청", description = "유저 정보 업데이트를 요청합니다.")
    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateMember(@PathVariable("userId") String userId, @Validated @RequestBody MemberUpdateRequest dto) {
        return ResponseEntity.ok(memberServiceImpl.updateMemberInfo(userId, dto));
    }

    @Operation(summary = "유저 삭제 요청", description = "유저 정보가 삭제됩니다.")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> remove(@PathVariable("userId") String userId, @Validated @RequestBody MemberRemoveRequest dto) throws URISyntaxException {
        URI redirectUri = new URI(logoutRedirectUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);

        return new ResponseEntity<>(memberServiceImpl.remove(userId, dto), httpHeaders, HttpStatus.SEE_OTHER);
    }

    @Operation(summary = "내 비밀번호 검증(확인)", description = "이메일과 비밀번호 입력 시 비밀번호가 맞는지 확인")
    @PostMapping("/{loginId}/password")
    public ResponseEntity<String> isMyPw(@PathVariable("loginId") String loginId, @Validated @RequestBody MemberCheckPasswordRequest dto) {
        return ResponseEntity.ok(memberServiceImpl.isMyPassword(loginId, dto));}

    @Operation(summary = "로그아웃", description = "해당 유저의 토큰 정보가 db에서 삭제 됩니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {

        URI redirectUri = new URI(logoutRedirectUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);

        return new ResponseEntity<>(memberServiceImpl.logout(request, response), httpHeaders, HttpStatus.SEE_OTHER);
    }

    // 아직 테스트 X -> 프론트 연결 후 테스트 진행
    @Operation(summary = "카카오 로그아웃", description = "카카오 OAUTH를 이용하여 로그인 합니다.")
    @GetMapping(value="/kakao-logout")
    public ResponseEntity<String> kakaoLogout(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {

        URI redirectUri = new URI(logoutRedirectUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);

        return new ResponseEntity<>(kaKaoMemberService.logout(request, response), httpHeaders, HttpStatus.SEE_OTHER);
    }

}