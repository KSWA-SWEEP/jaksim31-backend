package com.sweep.jaksim31.service;

import com.sweep.jaksim31.dto.login.LoginReqDTO;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.dto.token.TokenDTO;
import com.sweep.jaksim31.dto.token.TokenReqDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MemberService {
    ResponseEntity<MemberRespDTO> signup(MemberReqDTO memberRequestDto);
    ResponseEntity<TokenDTO> login(LoginReqDTO loginReqDTO, HttpServletResponse response);
    ResponseEntity<?> reissue(TokenReqDTO tokenReqDTO, HttpServletResponse response);
    ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<?> isMember(MemberEmailDto memberRequestDto);
    ResponseEntity<?> updatePw(String userId, MemberUpdateDTO memberUpdateDTO);
    ResponseEntity<MemberRespDTO> getMyInfo(String userId);
    ResponseEntity<?> updateMemberInfo(String userId, MemberUpdateDTO memberUpdateDTO);
    ResponseEntity<Boolean> isMyPassword(String userId, MemberIsMyPwDTO dto);
    ResponseEntity<String> remove(String userId, MemberRemoveDTO dto);
}
