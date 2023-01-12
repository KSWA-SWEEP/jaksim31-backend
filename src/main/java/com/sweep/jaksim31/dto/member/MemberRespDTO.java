package com.sweep.jaksim31.dto.member;

import com.sweep.jaksim31.entity.diary.Diary;
import com.sweep.jaksim31.entity.members.Members;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberRespDTO
 * author :  방근호
 * date : 2023-01-09
 * description : 회원 가입 후 확인을 위해 가입 된 사용자 정보 전송을 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRespDTO {

    private String id;
    private String loginId;
    private String username;

    public static MemberRespDTO of(Members members) {
        return new MemberRespDTO(members.getId().toString(), members.getLoginId(), members.getUsername());
    }
}