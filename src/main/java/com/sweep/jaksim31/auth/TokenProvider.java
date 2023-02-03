package com.sweep.jaksim31.auth;

import com.sweep.jaksim31.domain.auth.Authority;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.enums.AuthorityExceptionType;
import com.sweep.jaksim31.enums.JwtExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * packageName :  com.sweep.jaksim31.auth
 * fileName : TokenProvider
 * author :  방근호
 * date : 2023-01-09
 * description : 사용자 Token 생성 및 인증을 위한 Provider
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             Email -> LoginId
 * 2023-01-30           방근호             createTokenDto 제거
 */

@Slf4j
@Getter
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";

    private final long ACCESS_TOKEN_EXPIRE_TIME;            // 30분
    private final long REFRESH_TOKEN_EXPIRE_TIME;  // 7일

    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey ,
                         @Value("${jwt.access-token-expire-time}") long accessTime,
                         @Value("${jwt.refresh-token-expire-time}") long refreshTime
    ) {
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    protected String createToken(String loginId, Set<Authority> auth, long tokenValid) {
        // ex) sub : abc@abc.com
        Claims claims = Jwts.claims().setSubject(loginId);

        // ex)  auth : ROLE_USER,ROLE_ADMIN
        claims.put(AUTHORITIES_KEY,
                auth.stream()
                        .map(Authority::getAuthorityName)
                        .collect(Collectors.joining(","))
        );

        // 현재시간
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // 토큰 발행 유저 정보
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(new Date(now.getTime() + tokenValid)) // 토큰 만료시간
                .signWith(key, SignatureAlgorithm.HS512) // 키와 알고리즘 설정
                .compact();
    }

    /**
     *
     * @param loginId
     * @param auth
     * @return 엑세스 토큰 생성
     */
    public String createAccessToken(String loginId,Set<Authority> auth) {
        return this.createToken(loginId,auth,ACCESS_TOKEN_EXPIRE_TIME);
    }

    /**
     *
     * @param loginId
     * @param auth
     * @return 리프레시 토큰 생성
     */
    public String createRefreshToken(String loginId,Set<Authority> auth) {
        return this.createToken(loginId,auth,REFRESH_TOKEN_EXPIRE_TIME);
    }

    /**
     *
     * @param token
     * @return 토큰 값을 파싱하여 클레임에 담긴 LoginId 값을 가져온다.
     */
    public String getMemberLoginIdByToken(String token) {
        if (token == null)
            throw new BizException(JwtExceptionType.EMPTY_TOKEN);
        // 토큰의 claim 의 sub 키에 이메일 값이 들어있다.
        return this.parseClaims(token).getSubject();
    }

    public Authentication getAuthentication(String accessToken) throws BizException {

        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null || !StringUtils.hasText(claims.get(AUTHORITIES_KEY).toString())) {
            throw new BizException(AuthorityExceptionType.NOT_FOUND_AUTHORITY); // 유저에게 아무런 권한이 없습니다.
        }

        log.debug("claims.getAuth = {}",claims.get(AUTHORITIES_KEY));
        log.debug("claims.getLoginId = {}",claims.getSubject());

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        authorities.stream().forEach(o->{
            log.debug("getAuthentication -> authorities = {}",o.getAuthority());
        });

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new CustomLoginIdPasswordAuthToken(principal, "", authorities);
    }

    public int validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return 1;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            return 2;
        } catch (Exception e) {
            log.info("잘못된 토큰입니다.");
            return -1;
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) { // 만료된 토큰이 더라도 일단 파싱을 함
//            throw new BizException(JwtExceptionType.BAD_TOKEN);
            return e.getClaims();
        }
    }
}