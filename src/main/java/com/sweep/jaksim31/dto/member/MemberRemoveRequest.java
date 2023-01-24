package com.sweep.jaksim31.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberRemoveDTO -> MemberRemoveRequest
 * author :  방근호
 * date : 2023-01-09
 * description : 회원 탈퇴를 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 * 2023-01-12           방근호             클래스 이름 변경
 * 2023-01-17           방근호             필드(id->userId) 이름 변경
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRemoveRequest {
    private String userId;
    private String password;
}
