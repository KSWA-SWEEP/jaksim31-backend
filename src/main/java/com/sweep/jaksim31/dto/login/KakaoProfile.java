package com.sweep.jaksim31.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sweep.jaksim31.dto.login.KakaoLoginRequest;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.MemberSaveRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.xml.ws.BindingType;

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

    public String id;
    @JsonProperty("connected_at")
    public String connectedAt;
    public Properties properties;
    @JsonProperty("kakao_account")
    public KakaoAccount kakaoAccount;

    @Data
    @AllArgsConstructor
    public static class Properties { //(1)
        public String nickname;
        @JsonProperty("profile_image")
        public String profileImage; // 이미지 경로 필드1
        @JsonProperty("thumbnail_image")
        public String thumbnailImage;
    }

    @Data
    public class KakaoAccount { //(2)
        public Boolean profile_nickname_needs_agreement;
        public Boolean profile_image_needs_agreement;
        public Profile profile;
        public Boolean has_email;
        public Boolean email_needs_agreement;
        public Boolean is_email_valid;
        public Boolean is_email_verified;
        public String email;

        @Data
        public class Profile {
            public String nickname;
            public String thumbnail_image_url;
            public String profile_image_url; // 이미지 경로 필드2
            public Boolean is_default_image;
        }
    }

    public KakaoLoginRequest toLoginRequest() {
        return new KakaoLoginRequest(id, properties.nickname, properties.profileImage);
    }

}

