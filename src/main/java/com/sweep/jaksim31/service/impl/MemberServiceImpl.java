package com.sweep.jaksim31.service.impl;

import com.sweep.jaksim31.auth.TokenProvider;
import com.sweep.jaksim31.service.MemberService;
import com.sweep.jaksim31.util.HeaderUtil;
import com.sweep.jaksim31.entity.members.Members;
import com.sweep.jaksim31.entity.members.MemberRepository;
import com.sweep.jaksim31.dto.member.MemberIsMyPwDTO;
import com.sweep.jaksim31.dto.member.MemberRemoveDTO;
import com.sweep.jaksim31.dto.member.MemberRespDTO;
import com.sweep.jaksim31.dto.member.MemberUpdateDTO;
import com.sweep.jaksim31.util.exceptionhandler.BizException;
import com.sweep.jaksim31.util.exceptionhandler.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


/**
 * MemberService 설명 : 멤버 등록 및 조회
 * @version 1.0.0
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    /**
     * @param email
     * @return email에 해당하는 멤버의 정보를 반환한다.
     */
    @Override
    @Transactional(readOnly = true)
    public MemberRespDTO getMemberInfo(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberRespDTO::of)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER)); // 유저를 찾을 수 없습니다.
    }

    /**
     * @return 현재 securityContext에 있는 유저 정보를 반환한다.
     */
    @Override
    @Transactional(readOnly = true)
    public MemberRespDTO getMyInfo(HttpServletRequest request) {
        String accessToken = HeaderUtil.getAccessToken(request);
//        String accessToken = CookieUtil.getCookie(request, "access_token")
//                .map(Cookie::getValue)
//                .orElse((null));
//        System.out.println("ACCESS : " + accessToken);
        String email = tokenProvider.getMemberEmailByToken(accessToken);
        return memberRepository.findByEmail(email)
                .map(MemberRespDTO::of)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
    }


    /**
     * @param MemberUpdateDTO DirtyChecking 을 통한 멤버 업데이트 ( Email은 업데이트 할 수 없다.)
     */
    @Override
    @Transactional
    public void updateMemberInfo(MemberUpdateDTO dto) {
        Members members = memberRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        members.updateMember(dto, passwordEncoder);
    }

    @Transactional
    public boolean isMyPassword(MemberIsMyPwDTO dto){
        Members members = memberRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        if (passwordEncoder.matches(dto.getPassword(), members.getPassword())) return true;
        else return false;
    }

    @Override
    @Transactional
    public String remove(MemberRemoveDTO dto) {
        Members entity = memberRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        entity.remove('Y');
        memberRepository.save(entity);
        return entity.getEmail();
    }

}
