package com.sweep.jaksim31.service;

import com.sweep.jaksim31.dto.login.LoginReqDTO;
import com.sweep.jaksim31.dto.member.MemberEmailDto;
import com.sweep.jaksim31.dto.member.MemberReqDTO;
import com.sweep.jaksim31.dto.member.MemberRespDTO;
import com.sweep.jaksim31.dto.member.MemberUpdateDTO;
import com.sweep.jaksim31.dto.token.TokenDTO;
import com.sweep.jaksim31.dto.token.TokenReqDTO;
import com.sweep.jaksim31.entity.members.Members;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface AuthService {

    MemberRespDTO signup(MemberReqDTO memberRequestDto);
    TokenDTO login(LoginReqDTO loginReqDTO, HttpServletResponse response);
    TokenDTO reissue(TokenReqDTO tokenReqDTO, HttpServletResponse response);
    ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);
    Optional<Members> isMember(MemberEmailDto memberRequestDto);
    void updatePw(MemberUpdateDTO dto);

}
