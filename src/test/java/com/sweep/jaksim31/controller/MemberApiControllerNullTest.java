package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.domain.auth.AuthorityRepository;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.token.RefreshTokenRepository;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.exception.type.MemberExceptionType;
import com.sweep.jaksim31.service.impl.KakaoMemberServiceImpl;
import com.sweep.jaksim31.service.impl.MemberServiceImpl;
import com.sweep.jaksim31.utils.JsonUtil;
import com.sweep.jaksim31.utils.RedirectionUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * packageName :  com.sweep.jaksim31.controller
 * fileName : MemberApiControllerNullTest
 * author :  김주현
 * date : 2023-01-27
 * description : MemberApiController Validator test(NULL exception 확인)
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-27              김주현             최초 생성
 */

@WebMvcTest(controllers = MembersApiController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser // 401 에러 방지
class MemberApiControllerNullTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberServiceImpl memberService;

    @MockBean
    private KakaoMemberServiceImpl kaKaoMemberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private AuthorityRepository authorityRepository;
    @MockBean
    private RefreshTokenRepository refreshTokenRepository;
    @MockBean
    private DiaryRepository diaryRepository;

    @MockBean
    private RedirectionUtil redirectionUtil;

    MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
            .loginId("loginId")
            .userId("userId")
            .username("username")
            .profileImage("profileImage")
            .diaryTotal(10)
            .build();

    @Nested
    @DisplayName("SignUp Controller")
    class SignUp {
        @Test
        @DisplayName("사용자 아이디를 입력하지 않은 경우")
        public void invalidSingupNotFoundLoginId() throws Exception {
            //when
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest(null, "password", "geunho", "profileImage");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberSaveRequest);

            mockMvc.perform(post("/api/v0/members/register")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_LOGIN_ID.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_LOGIN_ID.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("비밀번호를 입력하지 않은 경우")
        public void invalidSingupNotFoundPassword() throws Exception {
            //when
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest("loginId", null, "geunho", "profileImage");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberSaveRequest);

            mockMvc.perform(post("/api/v0/members/register")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_PASSWORD.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_PASSWORD.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("사용자 이름울 입력하지 않은 경우")
        public void invalidSingupNotFoundUsername() throws Exception {
            //when
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest("loginId", "password", null, "profileImage");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberSaveRequest);

            mockMvc.perform(post("/api/v0/members/register")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_USERNAME.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_USERNAME.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("프로필 이미지가 없는 경우")
        public void invalidSingupNotFoundProfileImage() throws Exception {
            //when
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest("loginId", "password", "username", null);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberSaveRequest);

            mockMvc.perform(post("/api/v0/members/register")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_PROFILE_IMAGE.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_PROFILE_IMAGE.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }

    @Nested
    @DisplayName("Login Controller")
    class login {
        @Test
        @DisplayName("로그인 ID를 입력하지 않은 경우")
        void invalidLoginNotFoundLoginId() throws Exception {
            // when
            LoginRequest loginRequest = new LoginRequest(null, "password");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(loginRequest);

            mockMvc.perform(post("/api/v0/members/login")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_LOGIN_ID.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_LOGIN_ID.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("비밀번호를 입력하지 않은 경우")
        void invalidLoginNotFoundPassword() throws Exception {
            // when
            LoginRequest loginRequest = new LoginRequest("loginId", null);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(loginRequest);

            mockMvc.perform(post("/api/v0/members/login")
                            .with(csrf()) //403 에러 방지
                            .param("redirectUri", "http://adsaadsadadadad")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_PASSWORD.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_PASSWORD.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }


    @Nested
    @DisplayName("Reissue Controller")
    class Reissue {
        @Test
        @DisplayName("토큰 값이 비어있는 경우")
        void invalidReissueEmptyToken() throws Exception {
            //when
            mockMvc.perform(post("/api/v0/members/geunho/reissue")
                            .with(csrf()) //403 에러 방지
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }


    @Nested
    @DisplayName("isMember Controller")
    class IsMember {
        @Test
        @DisplayName("로그인 아이디를 입력하지 않은 경우")
        void invalidIsMemberNotFoundLoginId() throws Exception {
            //when
            MemberCheckLoginIdRequest memberCheckLoginIdRequest = new MemberCheckLoginIdRequest(null);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberCheckLoginIdRequest);

            mockMvc.perform(post("/api/v0/members")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_LOGIN_ID.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_LOGIN_ID.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }


    @Nested
    @DisplayName("Change Password Controller")
    class ChangePassword {
        @DisplayName("새로운 비밀번호가 입력되지 않은 경우")
        @Test
        void invalidChangePwNotFoundNewPassword() throws Exception {
            //when
            MemberUpdatePasswordRequest memberUpdatePasswordRequest = new MemberUpdatePasswordRequest(null);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberUpdatePasswordRequest);

            mockMvc.perform(put("/api/v0/members/string/password")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_NEW_PASSWORD.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_NEW_PASSWORD.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));

        }
    }

    // TODO Validator 확정 되면 테스트코드 추가
//    @DisplayName("UpdateMember Controller")
//    @Nested
//    class UpdateMember {
//
//    }

    @DisplayName("Remove Controller")
    @Nested
    class RemoveMember {
        @DisplayName("사용자 아이디가 입력되지 않은 경우")
        @Test
        void invalidRemoveMemberNotFoundUserId() throws Exception {
            //when
            MemberRemoveRequest memberRemoveRequest = new MemberRemoveRequest(null, "geunho");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberRemoveRequest);

            mockMvc.perform(delete("/api/v1/members/geunho")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_USER_ID.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_USER_ID.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @DisplayName("비밀번호가 입력되지 않은 경우")
        @Test
        void invalidRemoveMemberNotFoundPassword() throws Exception {
            //when
            MemberRemoveRequest memberRemoveRequest = new MemberRemoveRequest("geunho", null);
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberRemoveRequest);

            mockMvc.perform(delete("/api/v1/members/geunho")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_PASSWORD.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_PASSWORD.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }


    @Nested
    @DisplayName("isMyPw Controller")
    class IsMyPw {
        @DisplayName("비밀번호를 입력하지 않은 경우")
        @Test
        void invalidPasswordIsMyPasswordNotFoundPassword() throws Exception {
            MemberCheckPasswordRequest memberRemoveRequest = new MemberCheckPasswordRequest(null);
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberRemoveRequest);

            mockMvc.perform(post("/api/v1/members/geunho/password")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_PASSWORD.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_PASSWORD.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }
}