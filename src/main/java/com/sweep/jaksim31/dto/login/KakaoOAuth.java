package com.sweep.jaksim31.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/**
 * packageName :  com.sweep.jaksim31.domain
 * fileName : KakaoOAuth
 * author :  방근호
 * date : 2023-01-15
 * description : 카카오 인증 서버로 부터 토큰을 받을 객체
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-15           방근호                최초 생성
 */

@Data
public class KakaoOAuth {
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("refresh_token_expires_in")
    private int refreshTokenExpiresIn;
    private String scope;

}
