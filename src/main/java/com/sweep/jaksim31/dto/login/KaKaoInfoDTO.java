package com.sweep.jaksim31.dto.login;
import com.sweep.jaksim31.entity.members.KakaoMembers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class KaKaoInfoDTO {

    private String user_id;
    private static class properties {
        private static String nickname;
        public static String profile_image;
    }
    private static class profile {
        private static String email;
    }

    public KakaoMembers toMember(PasswordEncoder passwordEncoder) {
        return KakaoMembers.builder()
                .user_id(user_id)
                .email(profile.email)
                .username(properties.nickname)
                .profile_image(properties.profile_image)
                .register_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .update_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .delYn('N')
                .build();
    }


}
