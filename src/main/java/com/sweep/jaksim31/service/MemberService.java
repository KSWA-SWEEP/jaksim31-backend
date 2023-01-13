package com.sweep.jaksim31.service;

import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.dto.token.TokenResponse;
import com.sweep.jaksim31.dto.token.TokenRequest;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MemberService {
    ResponseEntity<MemberSaveResponse> signup(MemberSaveRequest memberRequestDto);
    ResponseEntity<TokenResponse> login(LoginRequest loginRequest, HttpServletResponse response);
    ResponseEntity<?> reissue(TokenRequest tokenRequest, HttpServletResponse response);
    ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<?> isMember(MemberCheckLoginIdRequest memberRequestDto);
    ResponseEntity<?> updatePw(String userId, MemberUpdateRequest memberUpdateRequest);
    ResponseEntity<MemberInfoResponse> getMyInfo(String userId);
    ResponseEntity<MemberInfoResponse> getMyInfoByLoginId(String loginId);
    ResponseEntity<?> updateMemberInfo(String userId, MemberUpdateRequest memberUpdateRequest);
    ResponseEntity<Boolean> isMyPassword(String userId, MemberCheckPasswordRequest dto);
    ResponseEntity<String> remove(String userId, MemberRemoveRequest dto);
}
