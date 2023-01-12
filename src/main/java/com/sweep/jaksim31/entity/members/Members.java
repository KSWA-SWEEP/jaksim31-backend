package com.sweep.jaksim31.entity.members;

import com.sweep.jaksim31.dto.member.MemberUpdateDTO;
import com.sweep.jaksim31.entity.auth.Authority;
import com.sweep.jaksim31.entity.auth.MemberAuth;
import com.sweep.jaksim31.entity.diary.Diary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sweep.jaksim31.entity.auth.MemberAuth.ROLE_USER;

/**
 * packageName :  com.sweep.jaksim31.entity.members
 * fileName : Members
 * author :  방근호
 * date : 2023-01-09
 * description : 사용자(Member) 객체
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 * 2023-01-12           김주현             profilePhoto -> profileImage
 */

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "member")
public class Members {

    @Id
    private ObjectId id;

    private String loginId; // 사용자 로그인 아이디
    private String password;
    private String username;

    private Boolean isSocial; // 소셜 로그인 사용자 여부
    private String profileImage;
    private List<Diary> recentDiaries;
    private int diaryTotal; // 총 일기 수
    private char delYn;
    @CreatedDate
    private Instant registerDate;
    @LastModifiedDate
    private Instant updateDate;

    private Set<Authority> authorities = new HashSet<>();


    @Builder
    public Members(String username, String loginId, String password, Boolean isSocial, char delYn, List<Diary> recentDiaries,
                   String profileImage, int diaryTotal, Set<Authority> authorities, Instant register_date, Instant update_date) {
        this.username = username;
        this.loginId = loginId;
        this.password = password;
        this.delYn = delYn;
        this.registerDate = register_date;
        this.updateDate = update_date;
        this.isSocial = isSocial;
        this.diaryTotal = diaryTotal;
        this.recentDiaries = recentDiaries;
        this.profileImage = profileImage;
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
        if(dto.getProfileImage() != null) this.profileImage = dto.getProfileImage();
        this.updateDate = Instant.now();
    }

    public void remove(char delYn){
        this.delYn = delYn;
    }
}
