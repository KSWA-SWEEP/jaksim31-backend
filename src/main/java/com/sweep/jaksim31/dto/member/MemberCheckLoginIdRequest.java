package com.sweep.jaksim31.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberRespDTO -> MemberCheckLoginIdRequest
 * author :  방근호
 * date : 2023-01-09
 * description : 사용자 로그인 ID(LoginId)로 가입 여부 조회를 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 * 2023-01-12           방근호             클래스 이름 변경
 * 2023-01-17           방근호             생성자 Annotation 추가
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCheckLoginIdRequest {
    private String loginId;
}
