package com.sweep.jaksim31.auth;

import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.enums.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * packageName :  com.sweep.jaksim31.auth
 * fileName : CustomLoginIdPasswordAuthProvider
 * author :  방근호
 * date : 2023-01-09
 * description : 사용자 인증을 위한 Provider
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-12           김주현       클래스 명 변경(CustomEmailPasswordAuthProvider -> CustomLoginIdPasswordAuthProvider)
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginIdPasswordAuthProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();


    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  CustomLoginIdPasswordAuthToken authentication) throws BizException {

        log.debug("additionalAuthenticationChecks authentication = {}",authentication);

        if (authentication.getCredentials() == null) {
            log.debug("additionalAuthenticationChecks is null !");
            throw new BizException(MemberExceptionType.NOT_FOUND_PASSWORD);
        }
        String presentedPassword = authentication.getCredentials().toString();
        log.debug("authentication.presentedPassword = {}",presentedPassword);

        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BizException(MemberExceptionType.WRONG_PASSWORD);
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws BizException {
        UserDetails user = null;
        try {
            user = retrieveUser(authentication.getName());
        }catch (BizException ex) {
            log.debug("error in retrieveUser = {}", ex.getMessage());
            throw ex;
        }

        Object principalToReturn = user;
        CustomLoginIdPasswordAuthToken result = new CustomLoginIdPasswordAuthToken(principalToReturn
                ,authentication.getCredentials()
                ,this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
        additionalAuthenticationChecks(user,result);
        result.setDetails(authentication.getDetails());
        return result;
    }

    protected final UserDetails retrieveUser(String username ) throws BizException {
        try {
            UserDetails loadedUser = customUserDetailsService.loadUserByUsername(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        }
        catch (BizException ex) {
            log.debug("error in retrieveUser = {}", ex.getMessage());
            throw ex;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(
                    "내부 인증 로직중 알수 없는 오류가 발생하였습니다.");
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CustomLoginIdPasswordAuthToken.class);
    }
}