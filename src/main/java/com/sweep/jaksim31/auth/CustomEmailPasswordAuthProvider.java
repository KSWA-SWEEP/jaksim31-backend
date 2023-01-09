package com.sweep.jaksim31.auth;

import com.sweep.jaksim31.util.exceptionhandler.BizException;
import com.sweep.jaksim31.util.exceptionhandler.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomEmailPasswordAuthProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();


    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  CustomEmailPasswordAuthToken authentication) throws BizException {

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
            throw ex;
        }

        Object principalToReturn = user;
        CustomEmailPasswordAuthToken result = new CustomEmailPasswordAuthToken(principalToReturn
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
        return authentication.equals(CustomEmailPasswordAuthToken.class);
    }
}