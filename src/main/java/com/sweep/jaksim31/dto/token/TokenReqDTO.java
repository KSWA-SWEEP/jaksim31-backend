package com.sweep.jaksim31.dto.token;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenReqDTO {
    private String accessToken;
    private String refreshToken;
}
