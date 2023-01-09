package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.service.impl.AuthServiceImpl;
import com.sweep.jaksim31.dto.member.MemberIsMyPwDTO;
import com.sweep.jaksim31.dto.member.MemberRemoveDTO;
import com.sweep.jaksim31.dto.member.MemberRespDTO;
import com.sweep.jaksim31.dto.member.MemberUpdateDTO;
import com.sweep.jaksim31.service.impl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "멤버", description = "멤버 관련 api 입니다.")
@RequestMapping("/v1/members")
public class MembersApiController {
    private final MemberServiceImpl memberServiceImpl;



//    @Operation(summary = "자신 정보 조회", description = "자신의 정보를 요청합니다.")
//    @PostMapping("")
//    public MemberRespDTO getMyInfo(HttpServletRequest request) {
//        return memberService.getMyInfo(request);
//    }

    @Operation(summary = "자신 정보 조회", description = "자신의 정보를 요청합니다.")
    @GetMapping("")
    public MemberRespDTO getMyInfo(HttpServletRequest request) {
        return memberServiceImpl.getMyInfo(request);
    }

    @Operation(summary = "이메일 정보 조회", description = "해당 이메일의 정보를 요청합니다.")
    @GetMapping("/{email}")
    public MemberRespDTO getMemberInfo(@PathVariable String email) {
        return memberServiceImpl.getMemberInfo(email);
    }

    @Operation(summary = "유저 정보 업데이트 요청", description = "유저 정보 업데이트를 요청합니다.")
    @PutMapping("")
    public void updateMember(@RequestBody MemberUpdateDTO dto) {
        memberServiceImpl.updateMemberInfo(dto);
    }

    @Operation(summary = "유저 삭제 요청", description = "유저 정보가 생성됩니다.")
    @DeleteMapping("")
    public String remove(@RequestBody MemberRemoveDTO dto) {
        return memberServiceImpl.remove(dto);
    }

    @Operation(summary = "내 비밀번호 검증(확인)", description = "이메일과 비밀번호 입력 시 비밀번호가 맞는지 확인")
    @PostMapping("/valid-pw")
    public boolean isMyPw(@RequestBody MemberIsMyPwDTO dto) {return memberServiceImpl.isMyPassword(dto);}

    private final AuthServiceImpl authServiceImpl;
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request, HttpServletResponse response) {

        return authServiceImpl.logout(request, response);
    }

}