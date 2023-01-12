package com.sweep.jaksim31.dto.member;

import com.sweep.jaksim31.entity.diary.Diary;
import com.sweep.jaksim31.entity.members.Members;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberInfoDTO
 * author :  김주현
 * date : 2023-01-11
 * description : 사용자 정보 조회를 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-11           김주현             최초 생성
 * 2023-01-12           김주현             profilePhoto -> profileImage
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoDTO {
    private String id;
    private String loginId;
    private String username;
    private String profileImage;
    private List<Diary> recentDiaries;
    private int diaryTotal;

    public static MemberInfoDTO of(Members members) {
        return new MemberInfoDTO(members.getId().toString(), members.getLoginId(), members.getUsername(), members.getProfileImage(), members.getRecentDiaries(), members.getDiaryTotal());
    }
}
