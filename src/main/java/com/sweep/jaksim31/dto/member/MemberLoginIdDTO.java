package com.sweep.jaksim31.dto.member;

import lombok.Data;
/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberRespDTO
 * author :  방근호
 * date : 2023-01-09
 * description : 사용자 로그인 ID(Email)로 가입 여부 조회를 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 */

@Data
public class MemberLoginIdDTO{
    private String loginId;
}
