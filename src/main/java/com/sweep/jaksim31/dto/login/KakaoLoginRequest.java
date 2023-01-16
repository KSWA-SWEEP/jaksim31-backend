package com.sweep.jaksim31.dto.login;
import com.sweep.jaksim31.dto.member.MemberSaveRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
 * 2023-01-15           방근호                필드 수정, 기존 로그인 요청을 확장(상속)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoLoginRequest extends LoginRequest {

    private String username;
    private String profileImage;

    @Override
    public MemberSaveRequest toMemberSaveRequest() {
        System.out.println();
        return MemberSaveRequest.builder()
                .loginId(super.getLoginId())
                .username(username)
                .password(super.getLoginId())
                .profileImage(profileImage)
                .build();
    }

    public KakaoLoginRequest(String loginId, String username, String profileImage){
        super(loginId, loginId);
        this.username = username;
        this.profileImage = profileImage;
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getLoginId() {
        return super.getLoginId();
    }
}


