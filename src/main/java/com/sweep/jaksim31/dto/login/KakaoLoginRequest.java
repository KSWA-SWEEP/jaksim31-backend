package com.sweep.jaksim31.dto.login;
import com.sweep.jaksim31.domain.members.Members;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * packageName :  com.sweep.jaksim31.dto/login
 * fileName : KaKaoInfoDTO
 * author :  장건
 * date : 2023-01-11
 * description : 카카오 로그인 정보들을 담기 위한 DTO
 * ==============================================================================================
 * DATE                 AUTHOR                                     NOTE
 * -----------------------------------------------------------------------------------------------
 * 2023-01-12            장건                                     최초 생성
 * 2023-01-13            장건                              Kakao Members 삭제
 * 2023-01-13            장건                Class 명 수정 (KaKaoInfoDTO -> KakaoLoginRequest)
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoLoginRequest {

    private String loginId;
    private String username;
    private String profileImage;

    public Members toMember(PasswordEncoder passwordEncoder) {
        return Members.builder()
                .loginId(loginId)
                .username(username)
                .password(passwordEncoder.encode(loginId))
                .profileImage(profileImage)
                .register_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .update_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .delYn('N')
                .build();
    }


}
