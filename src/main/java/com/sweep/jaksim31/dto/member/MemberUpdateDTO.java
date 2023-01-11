package com.sweep.jaksim31.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberUpdateDTO
 * author :  방근호
 * date : 2023-01-09
 * description : 사용자 정보 Update를 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateDTO {
    private String oldPassword;
    private String newPassword;
    private String username;
    private String profilePhoto;
//    private List<String> authorities;

}
