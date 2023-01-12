package com.sweep.jaksim31.dto.member;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberIsMyPwDTO
 * author :  방근호
 * date : 2023-01-09
 * description : 비밀번호 확인을 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 */

@Data
@NoArgsConstructor
public class MemberIsMyPwDTO {

    private String password;
}
