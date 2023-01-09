package com.sweep.jaksim31.entity.members;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sweep.jaksim31.dto.member.MemberUpdateDTO;
import com.sweep.jaksim31.entity.auth.Authority;
import com.sweep.jaksim31.entity.auth.MemberAuth;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sweep.jaksim31.entity.auth.MemberAuth.ROLE_USER;

@Getter
@NoArgsConstructor
@Document(collection = "member")
public class Members {

    @Id
    private String memberId;

    private String username;
    private String email;
    private String password;
    private char delYn;
    @CreatedDate
    private Instant register_date;
    @LastModifiedDate
    private Instant update_date;

    private Set<Authority> authorities = new HashSet<>();


    @Builder
    public Members(String username, String email, String password, char delYn,
                   Set<Authority> authorities, Instant register_date, Instant update_date) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.delYn = delYn;
        this.register_date = register_date;
        this.update_date = update_date;
        this.addAuthority(new Authority(username, MemberAuth.of("ROLE_USER")));
        System.out.println();
    }

    public void addAuthority(Authority authority) {
        this.getAuthorities().add(authority);
    }

    public void removeAuthority(Authority authority) {
        this.getAuthorities().remove(authority);
    }

    public String getAuthoritiesToString() {
        return this.authorities.stream()
                .map(Authority::getAuthorityName)
                .collect(Collectors.joining(","));
    }

    public void updateMember(MemberUpdateDTO dto, PasswordEncoder passwordEncoder) throws Exception {

        if(dto.getOldPassword() != null) {
            // 비밀번호가 같지 않다면
            if (!passwordEncoder.matches(dto.getOldPassword(), this.password)){
                throw new Exception("비밀 번호가 불일치 합니다.");
            }
            this.password = passwordEncoder.encode(dto.getNewPassword());
        }
        if(dto.getUsername() != null) this.username = dto.getUsername();
        this.update_date = Instant.now();
    }

    public void remove(char delYn){
        this.delYn = delYn;
    }
}
