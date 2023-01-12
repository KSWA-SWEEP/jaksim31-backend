package com.sweep.jaksim31.dto.token;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {
    private String loginId;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String expTime;
}