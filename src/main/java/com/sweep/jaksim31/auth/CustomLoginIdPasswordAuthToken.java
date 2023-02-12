package com.sweep.jaksim31.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * packageName :  com.sweep.jaksim31.auth
 * fileName : CustomLoginIdPasswordAuthToken
 * author :  방근호
 * date : 2023-01-09
 * description : 사용자 Token 인증을 위한 Provider
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-12           김주현       클래스 명 변경(CustomEmailPasswordAuthToken -> CustomLoginIdPasswordAuthToken)
 */

public class CustomLoginIdPasswordAuthToken extends AbstractAuthenticationToken {

    private final Object principal; // NOSONAR

    private final Object credentials; // NOSONAR

    public CustomLoginIdPasswordAuthToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public CustomLoginIdPasswordAuthToken(Object principal, Object credentials,
                                          Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
