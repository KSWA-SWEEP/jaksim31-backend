package com.sweep.jaksim31.dto.token;

import lombok.*;

/**
 * packageName :  com.sweep.jaksim31.dto.token
 * fileName : TokenResponse
 * author :  방근호
 * date : 2023-01-13
 * description : 토큰 관련 정보 응답 dto
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
    private String loginId;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String expTime;
}