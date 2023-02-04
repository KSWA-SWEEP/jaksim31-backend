package com.sweep.jaksim31.dto.member;

import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.dto.diary.DiaryInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
 * 2023-01-31           김주현             recentDiaries -> recentDiary(DiaryInfoResponse)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberInfoResponse {
    private String userId;
    private String loginId;
    private String username;
    private String profileImage;
    private DiaryInfoResponse recentDiary;
    private int diaryTotal;

    public static MemberInfoResponse of(Members members) {
        return new MemberInfoResponse(members.getId(), members.getLoginId(), members.getUsername(), members.getProfileImage(), members.getRecentDiary(), members.getDiaryTotal());
    }
}
