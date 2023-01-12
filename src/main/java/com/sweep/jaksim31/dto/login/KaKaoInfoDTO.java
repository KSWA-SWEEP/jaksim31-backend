package com.sweep.jaksim31.dto.login;
import com.sweep.jaksim31.entity.members.Members;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KaKaoInfoDTO {

    private String loginId;
    private String userName;
    private String profileImage;

    public Members toMember() {
        return Members.builder()
                .loginId(loginId)
                .username(userName)
                .profileImage(profileImage)
                .register_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .update_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .delYn('N')
                .build();
    }


}
