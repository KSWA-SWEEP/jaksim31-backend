package com.sweep.jaksim31.dto.member;

import com.sweep.jaksim31.domain.diary.Diary;
import com.sweep.jaksim31.domain.members.Members;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberInfoDTO -> MemberInfoResponse
 * author :  김주현
 * date : 2023-01-11
 * description : 사용자 정보 조회를 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-11           김주현             최초 생성
 * 2023-01-12           김주현             profilePhoto -> profileImage
 *                      김주현             id -> userId
 *                      방근호             클래스 이름 변경
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponse {
    private String userId;
    private String loginId;
    private String username;
    private String profileImage;
    private List<Diary> recentDiaries;
    private int diaryTotal;

    public static MemberInfoResponse of(Members members) {
        return new MemberInfoResponse(members.getId().toString(), members.getLoginId(), members.getUsername(), members.getProfileImage(), members.getRecentDiaries(), members.getDiaryTotal());
    }
}
