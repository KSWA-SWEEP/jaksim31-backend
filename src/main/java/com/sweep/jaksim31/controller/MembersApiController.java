package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.dto.token.TokenResponse;
import com.sweep.jaksim31.dto.token.TokenRequest;
import com.sweep.jaksim31.service.impl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "멤버", description = "멤버 관련 api 입니다.")
@RequestMapping("/v0/members")
public class MembersApiController {
    private final MemberServiceImpl memberServiceImpl;

    @Value("${jwt.refresh-token-expire-time}")
    private long rtkLive;

    @GetMapping("/test")
    public String test(){
        return "OK";
    }

    @Operation(summary = "회원가입", description = "")
    @PostMapping("/register")
    public ResponseEntity<MemberSaveResponse> signup(@RequestBody MemberSaveRequest memberRequestDto) {
        log.debug("memberRequestDto = {}",memberRequestDto);
        return memberServiceImpl.signup(memberRequestDto);
    }

    @Operation(summary = "로그인", description = "유저 정보를 통해 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        return memberServiceImpl.login(loginRequest, response);
    }

    @Operation(summary = "회원가입 여부 확인", description = "이메일을 통해 회원가입 여부를 확인합니다.")
    @PostMapping("")
    public ResponseEntity<?> isMember(
            @RequestBody MemberCheckLoginIdRequest memberRequestDto) {
        return memberServiceImpl.isMember(memberRequestDto);
    }

    @Operation(summary = "토큰 재발급", description = "리프레쉬 토큰으로 토큰을 재발급 합니다.")
    @PostMapping("/{userId}/reissue")
    public ResponseEntity<?> reissue(@PathVariable("userId") String userId, @RequestBody TokenRequest tokenRequest,
                            HttpServletResponse response
    ) {
        return memberServiceImpl.reissue(tokenRequest, response);
    }

//    @Hidden
    @Operation(summary = "비밀번호 변경", description = "비밀번호 재설정을 요청합니다.")
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> changePw(@PathVariable("userId") String userId, @RequestBody MemberUpdateRequest memberUpdateRequest) {
        return memberServiceImpl.updatePw(userId, memberUpdateRequest);
    }

//    @Hidden
    @Operation(summary = "LoginID로 사용자 정보 조회", description = "Login ID로 자신의 정보를 요청합니다.")
    @GetMapping("")
    public ResponseEntity<MemberInfoResponse> getMyInfoByLoginId(@RequestParam("loginId") String loginId) {
        return memberServiceImpl.getMyInfoByLoginId(loginId);
    }

    // ================================================== //
    // ================================================== //
    // ================================================== //

    @Operation(summary = "개별 정보 조회", description = "자신의 정보를 요청합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<MemberInfoResponse> getMyInfo(@PathVariable("userId") String userId) {
        return memberServiceImpl.getMyInfo(userId);
    }

    @Operation(summary = "유저 정보 업데이트 요청", description = "유저 정보 업데이트를 요청합니다.")
    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateMember(@PathVariable("userId") String userId, @RequestBody MemberUpdateRequest dto) {
        return memberServiceImpl.updateMemberInfo(userId, dto);
    }

    @Operation(summary = "유저 삭제 요청", description = "유저 정보가 삭제됩니다.")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> remove(@PathVariable("userId") String userId, @RequestBody MemberRemoveRequest dto) {
        return memberServiceImpl.remove(userId, dto);
    }

    @Operation(summary = "내 비밀번호 검증(확인)", description = "이메일과 비밀번호 입력 시 비밀번호가 맞는지 확인")
    @PostMapping("/{userId}/password")
    public ResponseEntity<Boolean> isMyPw(@PathVariable("userId") String userId, @RequestBody MemberCheckPasswordRequest dto) {
        return memberServiceImpl.isMyPassword(userId, dto);}

    @Operation(summary = "로그아웃", description = "해당 유저의 토큰 정보가 db에서 삭제 됩니다.")
    @PostMapping("/{userId}/logout")
    public ResponseEntity<?> logout(@PathVariable("userId") String userId,
            HttpServletRequest request, HttpServletResponse response) {
        return memberServiceImpl.logout(request, response);
    }

}