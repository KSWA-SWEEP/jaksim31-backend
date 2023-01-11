package com.sweep.jaksim31.dto.member;

import com.sweep.jaksim31.entity.members.Members;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * packageName :  com.sweep.jaksim31.dto.member
 * fileName : MemberReqDTO
 * author :  방근호
 * date : 2023-01-09
 * description : 회원 가입 정보를 받기 위한 DTO
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberReqDTO {
    private String loginId;
    private String password;
    private String username;
    private String profilePhoto;

    public Members toMember(PasswordEncoder passwordEncoder) {
        return Members.builder()
                .loginId(loginId)
                .username(username)
                .password(passwordEncoder.encode(password))
                .register_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .update_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .delYn('N')
                .isSocial(false)
                .recentDiaries(new ArrayList<>())
                .profilePhoto(profilePhoto)
                .build();
    }


}
