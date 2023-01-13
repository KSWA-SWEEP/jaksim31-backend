package com.sweep.jaksim31.dto.member;

import com.sweep.jaksim31.domain.members.Members;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberRespDTO -> MemberSaveResponse
 * author :  방근호
 * date : 2023-01-09
 * description : 회원 가입 후 확인을 위해 가입 된 사용자 정보 전송을 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 * 2023-01-12           김주현             id -> userId
 * 2023-01-12           방근호             클래스 이름 변경
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSaveResponse {

    private String userId;
    private String loginId;
    private String username;

    public static MemberSaveResponse of(Members members) {
        return new MemberSaveResponse(members.getId().toString(), members.getLoginId(), members.getUsername());
    }
}