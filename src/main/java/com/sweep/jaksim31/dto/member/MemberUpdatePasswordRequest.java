package com.sweep.jaksim31.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberUpdatePasswordRequest
 * author :  방근호
 * date : 2023-01-16
 * description : 사용자 패스워드 Update를 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-16           방근호             최초 생성
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdatePasswordRequest {
    private String newPassword;
//    private List<String> authorities;

}
