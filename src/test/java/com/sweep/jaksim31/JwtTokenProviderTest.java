package com.sweep.jaksim31;

import com.sweep.jaksim31.auth.TokenProvider;
import com.sweep.jaksim31.domain.auth.Authority;
import com.sweep.jaksim31.domain.auth.MemberAuth;
import com.sweep.jaksim31.exception.BizException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@SpringBootTest    // 1
@Nested
@DisplayName("JWT 관련 테스트")
class JwtTokenProviderTest {
    @Autowired
    private TokenProvider jwtTokenProvider;

    @Value("${jwt.secret}")
    private String secretKey;	// 3

    private final TokenProvider invalidSecretKeyJwtTokenProvider
            = new TokenProvider(
            "invalidSecretKeyInvalidSecretKeyInvalidSecretKeyInvalidSecretKeylidSecretKeyInvalidSecretKeylidSecretKeyInvalidSecretKeylidSecretKeyInvalidSecretKey",
            8640000L,
            19000000L
    );	// 4

    static Set<Authority> authorities = new HashSet<>();

    @BeforeAll
    static void init() {
        authorities.add(new Authority("geunho", MemberAuth.of("ROLE_USER")));
    }

    @Test
    @DisplayName("토큰 생성 테스트")	// 5
    void createToken() {

        final String payload = String.valueOf(1L);

        final String token = jwtTokenProvider.createAccessToken(payload, authorities);

        assertThat(token).isNotNull();
    }

    @DisplayName("올바른 토큰 정보 조회")	// 6
    @Test
    void getPayloadByValidToken() {
        final String payload = String.valueOf(1L);

        final String token = jwtTokenProvider.createAccessToken(payload, authorities);

        assertThat(jwtTokenProvider.getMemberLoginIdByToken(token)).isEqualTo(payload);
    }

    @DisplayName("유효하지 않은 토큰 처리 테스트")	// 7
    @Test
    void getPayloadByInvalidToken() {
        assertThatExceptionOfType(BizException.class)
                .isThrownBy(() -> jwtTokenProvider.getMemberLoginIdByToken(null));
    }

    @DisplayName("만료된 토큰 처리 테스트")
    @Test
    void getPayloadByExpiredToken() {
        final String expiredToken = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .setSubject(String.valueOf(1L))
                .setExpiration(new Date((new Date()).getTime() - 1))	// 8
                .compact();

        assertThatExceptionOfType(SignatureException.class)
                .isThrownBy(() -> jwtTokenProvider.getMemberLoginIdByToken(expiredToken));
    }

    @DisplayName("다른 암호키로 생성된 토큰 처리 테스트")
    @Test
    void getPayloadByWrongSecretKeyToken() {
        final String invalidSecretToken = invalidSecretKeyJwtTokenProvider.createAccessToken(String.valueOf(1L), authorities);	// 9
        assertThatExceptionOfType(SignatureException.class)
                .isThrownBy(() -> jwtTokenProvider.getMemberLoginIdByToken(invalidSecretToken));
    }
}