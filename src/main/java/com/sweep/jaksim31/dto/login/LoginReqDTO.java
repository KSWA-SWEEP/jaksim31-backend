package com.sweep.jaksim31.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
/**
 * LoginReqDTO 설명 : 로그인시 DTO 이메일,패스워드
 * @author jowonjun
 * @version 1.0.0
 * 작성일 : 2022/01/23
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginReqDTO {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

}
