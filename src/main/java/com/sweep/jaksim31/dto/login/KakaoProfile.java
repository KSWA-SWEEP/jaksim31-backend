package com.sweep.jaksim31.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
* packageName :  com.sweep.jaksim31.domain
* fileName : KakaoProfile
* author :  방근호
* date : 2023-01-15
* description : 카카오 인증 서버로 부터 받는 유저 정보를 담을 객체
* ===========================================================
* DATE                 AUTHOR                NOTE
* -----------------------------------------------------------
* 2023-01-15           방근호                최초 생성
*/
@Data
@Builder
public class KakaoProfile {

    private String id;
    @JsonProperty("connected_at")
    private String connectedAt;
    private Properties properties;
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Data
    @AllArgsConstructor
    public static class Properties { //(1)
        private String nickname;
        @JsonProperty("profile_image")
        private String profileImage; // 이미지 경로 필드1
        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

    @Data
    public static class KakaoAccount { //(2)
        @JsonProperty("profile_nickname_needs_agreement")
        private Boolean profileNicknameNeedsAgreement;
        @JsonProperty("profile_image_needs_agreement")
        private Boolean profileImageNeedsAgreement;
        private Profile profile;
        @JsonProperty("has_email")
        private Boolean hasEmail;
        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;
        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;
        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;
        private String email;

        @Data
        public static class Profile {
            private String nickname;
            @JsonProperty("thumbnail_image_url")
            private String thumbnailImageUrl;
            @JsonProperty("profile_image_url")
            private String profileImageUrl; // 이미지 경로 필드2
            @JsonProperty("is_default_image")
            private Boolean isDefaultImage;
        }
    }

    public KakaoLoginRequest toLoginRequest() {
        return new KakaoLoginRequest(id, properties.nickname, properties.profileImage);
    }

}

