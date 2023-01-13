package com.sweep.jaksim31.dto.member;

import com.sweep.jaksim31.domain.members.Members;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberReqDTO -> MemberSaveRequest
 * author :  방근호
 * date : 2023-01-09
 * description : 회원 가입 정보를 받기 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 * 2023-01-12           김주현             profilePhoto -> profileImage / diaryTotal 추가
 * 2023-01-12           방근호             클래스 이름 변경
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSaveRequest {
    private String loginId;
    private String password;
    private String username;
    private String profileImage;

    public Members toMember(PasswordEncoder passwordEncoder) {
        return Members.builder()
                .loginId(loginId)
                .username(username)
                .password(passwordEncoder.encode(password))
                .register_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .update_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .delYn('N')
                .isSocial(false)
                .diaryTotal(0)
                .recentDiaries(new ArrayList<>())
                .profileImage(profileImage)
                .build();
    }


}
