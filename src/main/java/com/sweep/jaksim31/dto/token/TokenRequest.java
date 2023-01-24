package com.sweep.jaksim31.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName :  com.sweep.jaksim31.dto.token
 * fileName : TokenRequest
 * author :  방근호
 * date : 2023-01-13
 * description : 토큰 관련 api 요청 dto
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */

@Data
@AllArgsConstructor
public class TokenRequest {
    private String accessToken;
    private String refreshToken;
}
