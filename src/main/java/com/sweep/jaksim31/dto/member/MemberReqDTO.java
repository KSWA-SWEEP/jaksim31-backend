package com.sweep.jaksim31.dto.member;

import com.sweep.jaksim31.entity.members.Members;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberReqDTO {
    private String email;
    private String password;
    private String username;


    public Members toMember(PasswordEncoder passwordEncoder) {
        return Members.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .register_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .update_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .delYn('N')
                .build();
    }


}
