package com.sweep.jaksim31.service;

import com.sweep.jaksim31.dto.member.MemberIsMyPwDTO;
import com.sweep.jaksim31.dto.member.MemberRemoveDTO;
import com.sweep.jaksim31.dto.member.MemberRespDTO;
import com.sweep.jaksim31.dto.member.MemberUpdateDTO;

import javax.servlet.http.HttpServletRequest;

public interface MemberService {
    MemberRespDTO getMemberInfo(String email);
    MemberRespDTO getMyInfo(HttpServletRequest request);
    void updateMemberInfo(MemberUpdateDTO dto);
    boolean isMyPassword(MemberIsMyPwDTO dto);
    String remove(MemberRemoveDTO dto);
}
