package com.sweep.jaksim31.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberRemoveDTO
 * author :  방근호
 * date : 2023-01-09
 * description : 회원 탈퇴를 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRemoveDTO {
    private String id;
    private String password;
}
