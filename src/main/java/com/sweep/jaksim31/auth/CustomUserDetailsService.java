package com.sweep.jaksim31.auth;

import com.sweep.jaksim31.entity.auth.Authority;
import com.sweep.jaksim31.entity.members.MemberRepository;
import com.sweep.jaksim31.entity.members.Members;
import com.sweep.jaksim31.util.exceptionhandler.BizException;
import com.sweep.jaksim31.util.exceptionhandler.MemberExceptionType;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws BizException {
        log.debug("CustomUserDetailsService -> email = {}",email);
        return memberRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
    }

    @Transactional(readOnly = true)
    public Members getMember(String email) throws BizException {
        return memberRepository.findByEmail(email)
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
                members.getEmail(),
                members.getPassword(),
                authList
        );
    }
}