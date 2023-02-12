package com.sweep.jaksim31.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberUpdateDTO -> MemberUpdateRequest
 * author :  방근호
 * date : 2023-01-09
 * description : 사용자 정보 Update를 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 * 2023-01-12           김주현             profilePhoto -> profileImage
 * 2023-01-13           방근호             클래스 이름 변경
 * 2023-01-17           방근호             필드 제거 (password)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequest {
    private String username;
    private String profileImage;

}
