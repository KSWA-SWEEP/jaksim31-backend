package com.sweep.jaksim31.entity.members;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sweep.jaksim31.dto.member.MemberUpdateDTO;
import com.sweep.jaksim31.entity.auth.Authority;
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

@Getter
@NoArgsConstructor
@Document(collection = "member")
public class KakaoMembers {

    @Id
    private String user_id;

    private String username;
    private String email;
    private String profile_image;
    private char delYn;
    @CreatedDate
    private Instant register_date;
    @LastModifiedDate
    private Instant update_date;

    private Set<Authority> authorities = new HashSet<>();


    @Builder
    public KakaoMembers(String user_id, String username, String email, String profile_image, char delYn,
                   Set<Authority> authorities, Instant register_date, Instant update_date) {
        this.user_id = user_id;
        this.email = email;
        this.username = username;
        this.profile_image = profile_image;
        this.delYn = delYn;
        this.register_date = register_date;
        this.update_date = update_date;
        this.authorities = authorities;
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

    public void updateMember(MemberUpdateDTO dto, PasswordEncoder passwordEncoder) {
        if(dto.getUsername() != null) this.user_id = dto.getUsername();
        this.update_date = Instant.now();
    }

    public void remove(char delYn){
        this.delYn = delYn;
    }
}
