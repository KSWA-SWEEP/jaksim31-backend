package com.sweep.jaksim31.auth;

import com.sweep.jaksim31.domain.auth.Authority;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
/**
 * packageName :  com.sweep.jaksim31.auth
 * fileName : CustomUserDetailsService
 * author :  방근호
 * date : 2023-01-09
 * description : Customizing User Detail Service
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             email -> loginId
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String loginId) throws BizException {
        log.debug("CustomUserDetailsService -> loginId = {}",loginId);
        return memberRepository.findByLoginId(loginId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
    }

    @Transactional(readOnly = true)
    public Members getMember(String loginId) throws BizException {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(()->new BizException(MemberExceptionType.NOT_FOUND_USER));
    }

    // DB 에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(Members members) {

        // Collections<? extends GrantedAuthority>
        List<SimpleGrantedAuthority> authList = members.getAuthorities()
                .stream()
                .map(Authority::getAuthorityName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        authList.forEach(o-> log.debug("authList -> {}",o.getAuthority()));

        return new User(
                members.getLoginId(),
                members.getPassword(),
                authList
        );
    }
}