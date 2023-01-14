package com.sweep.jaksim31.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
/**
 * packageName :  com.sweep.jaksim31.dto.login
 * fileName : LoginReqDTO
 * author :  방근호
 * date : 2023-01-09
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-12           김주현             field 명 수정(email -> loginId)
 * 2023-01-13            장건              Class 명 수정 (LoginReqDTO -> LoginRequest)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

}
