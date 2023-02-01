package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.domain.auth.AuthorityRepository;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.token.RefreshTokenRepository;
import com.sweep.jaksim31.dto.login.KakaoProfile;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.JwtExceptionType;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * packageName :  com.sweep.jaksim31.controller
 * fileName : DiaryApiControllerTest
 * author :  방근호
 * date : 2023-01-17
 * description : Member Controller 단위 테스트
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-17           방근호             최초 생성
 * 2023-01-27           김주현             로그인 응답 코드 변경에 따른 test코드 수정
 * 2023-01-30           방근호             인증 로직 변경으로 인한 test 수정 및 제거
 * 2023-01-31           김주현             사용자 정보 조회, 수정 service 수정으로 인한 테스트 코드 수정
 * 2023-02-01           김주현             PathValue validation 추가로 인한 test 수정
 */

@WebMvcTest(controllers = MembersApiController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser // 401 에러 방지
class MembersApiControllerTest {

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
        @DisplayName("정상인 경우")
        public void singup() throws Exception {
            //given
            given(memberService.signup(any()))
                    .willReturn(MemberSaveResponse.builder()
                            .userId("userId")
                            .loginId("loginId")
                            .username("geunho")
                            .build());

            //when
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest("loginId", "password", "geunho", "profileImage");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberSaveRequest);

            mockMvc.perform(post("/api/v0/members/register")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId", Matchers.is("userId")))
                    .andExpect(jsonPath("$.loginId", Matchers.is("loginId")))
                    .andExpect(jsonPath("$.username", Matchers.is("geunho")))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("사용자 아이디를 입력하지 않은 경우")
        public void invalidSingupNotFoundLoginId() throws Exception {
            //when
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest("", "password", "geunho", "profileImage");
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
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest("loginId", "", "geunho", "profileImage");
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
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest("loginId", "password", "", "profileImage");
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
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest("loginId", "password", "username", "");
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
        @DisplayName("정상인 경우")
        void login() throws Exception {
            //given
            given(memberService.login(any(), any()))
                    .willReturn("로그인이 완료되었습니다.");

            // when
            LoginRequest loginRequest = new LoginRequest("loginId", "password");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(loginRequest);

            mockMvc.perform(post("/api/v0/members/login")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("비밀번호가 불일치할 경우")
        void invalidLogin() throws Exception {
            //given
            given(memberService.login(any(), any()))
                    .willThrow(new BizException(MemberExceptionType.WRONG_PASSWORD));

            // when
            LoginRequest loginRequest = new LoginRequest("loginId", "password");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(loginRequest);

            mockMvc.perform(post("/api/v0/members/login")
                            .with(csrf()) //403 에러 방지
                            .param("redirectUri", "http://adsaadsadadadad")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is("WRONG_PASSWORD")))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is("비밀번호를 잘못 입력하였습니다.")))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("로그인 ID를 입력하지 않은 경우")
        void invalidLoginNotFoundLoginId() throws Exception {
            // when
            LoginRequest loginRequest = new LoginRequest("", "password");
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
            LoginRequest loginRequest = new LoginRequest("loginId", "");
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
        @DisplayName("정상인 경우")
        void reissue() throws Exception {
            //given
//            given(memberService.reissue(any(), any()))
//                    .willReturn();
            //when
            mockMvc.perform(post("/api/v1/members/testobjectidtestobject12/reissue")
                            .with(csrf()) //403 에러 방지
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out));

        }
        @Test
        @DisplayName("정상인 경우(refresh token만 있는 경우)")
        void reissueRefreshTokenOnly() throws Exception {
            //given
//            given(memberService.reissue(any(), any()))
//                    .willReturn("토큰 재발급이 완료되었습니다.");

            //when
            mockMvc.perform(post("/api/v1/members/testobjectidtestobject12/reissue")
                            .with(csrf()) //403 에러 방지
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out));

        }

//        @Test
//        @DisplayName("토큰 값이 비어있는 경우")
//        void invalidReissueEmptyToken() throws Exception {
//            //when
//            mockMvc.perform(post("/api/v0/members/testobjectidtestobject12/reissue")
//                            .with(csrf()) //403 에러 방지
//                            .contentType(MediaType.APPLICATION_JSON))
//                    //then
//                    .andExpect(status().is4xxClientError())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("$.errorCode", Matchers.is(JwtExceptionType.EMPTY_TOKEN.getErrorCode())))
//                    .andExpect(jsonPath("$.errorMessage", Matchers.is(JwtExceptionType.EMPTY_TOKEN.getMessage())))
//                    .andDo(MockMvcResultHandlers.print(System.out));
//
//        }

    }


    @Nested
    @DisplayName("isMember Controller")
    class IsMember {
        @Test
        @DisplayName("정상인 경우")
        void isMember() throws Exception {
            //given
            given(memberService.isMember(any()))
                    .willReturn("test ok");
            //when
            MemberCheckLoginIdRequest memberCheckLoginIdRequest = new MemberCheckLoginIdRequest("string");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberCheckLoginIdRequest);

            mockMvc.perform(post("/api/v0/members")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string("test ok"))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("유저가 없는 경우")
        void invalidIsMember() throws Exception {
            //given
            given(memberService.isMember(any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));
            //when
            MemberCheckLoginIdRequest memberCheckLoginIdRequest = new MemberCheckLoginIdRequest("string");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberCheckLoginIdRequest);

            mockMvc.perform(post("/api/v0/members")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("로그인 아이디를 입력하지 않은 경우")
        void invalidIsMemberNotFoundLoginId() throws Exception {
            //when
            MemberCheckLoginIdRequest memberCheckLoginIdRequest = new MemberCheckLoginIdRequest("");
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
        @DisplayName("정상인 경우")
        @Test
        void changePw() throws Exception {

            given(memberService.updatePassword(any(), any()))
                    .willReturn("회원 정보가 정상적으로 변경되었습니다.");

            //when
            MemberUpdatePasswordRequest memberUpdatePasswordRequest = new MemberUpdatePasswordRequest("string");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberUpdatePasswordRequest);

            mockMvc.perform(put("/api/v0/members/string/password")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string("회원 정보가 정상적으로 변경되었습니다."))
                    .andDo(MockMvcResultHandlers.print(System.out));

        }

        @DisplayName("해당 유저가 없는 경우")
        @Test
        void invalidChangePw() throws Exception {

            given(memberService.updatePassword(any(), any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when
            MemberUpdatePasswordRequest memberUpdatePasswordRequest = new MemberUpdatePasswordRequest("string");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberUpdatePasswordRequest);

            mockMvc.perform(put("/api/v0/members/string/password")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));

        }

        @DisplayName("새로운 비밀번호가 입력되지 않은 경우")
        @Test
        void invalidChangePwNotFoundNewPassword() throws Exception {
            //when
            MemberUpdatePasswordRequest memberUpdatePasswordRequest = new MemberUpdatePasswordRequest("");
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

    @Nested
    @DisplayName("GetMyInfoByUserId Controller")
    class GetMyInfoByUserId {
        @DisplayName("정상인 경우")
        @Test
        void getMyInfoByUserId() throws Exception {
            given(memberService.getMyInfo(any(), any()))
                    .willReturn(MemberInfoResponse.builder()
                            .loginId("loginId")
                            .userId("userId")
                            .username("username")
                            .profileImage("profileImage")
                            .diaryTotal(10)
                            .build());

            //when
            mockMvc.perform(get("/api/v1/members/testobjectidtestobject12")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId", Matchers.is("userId")))
                    .andExpect(jsonPath("$.loginId", Matchers.is("loginId")))
                    .andExpect(jsonPath("$.username", Matchers.is("username")))
                    .andExpect(jsonPath("$.profileImage", Matchers.is("profileImage")))
                    .andExpect(jsonPath("$.diaryTotal", Matchers.is(10)))
                    .andExpect(jsonPath("$.recentDiary", Matchers.nullValue()))
                    .andDo(MockMvcResultHandlers.print(System.out));

        }

        @DisplayName("해당 유저가 없는 경우")
        @Test
        void invalidGetMyInfoByUserId() throws Exception {

            given(memberService.getMyInfo(any(),any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when
            mockMvc.perform(get("/api/v1/members/testobjectidtestobject12")
                            .with(csrf())) //403 에러 방지

                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));

        }
    }

    // TODO Validator 확정 되면 테스트코드 추가
    @DisplayName("UpdateMember Controller")
    @Nested
    class UpdateMember {
        @DisplayName("정상인 경우")
        @Test
        void updateMember() throws Exception {
            given(memberService.updateMemberInfo(any(), any(), any()))
                    .willReturn("회원 정보가 변경 되었습니다.");

            //when
            MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("방근호", "프로필이미지");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberUpdateRequest);

            mockMvc.perform(patch("/api/v1/members/testobjectidtestobject12")
                            .with(csrf())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string("회원 정보가 변경 되었습니다."))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @DisplayName("해당 유저가 없는 경우")
        @Test
        void invalidUpdateMember() throws Exception {
            given(memberService.updateMemberInfo(any(), any(), any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when
            MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("방근호", "프로필이미지");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberUpdateRequest);

            mockMvc.perform(patch("/api/v1/members/testobjectidtestobject12")
                            .with(csrf())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }

    @DisplayName("Remove Controller")
    @Nested
    class RemoveMember {
        @DisplayName("정상인 경우")
        @Test
        void remove() throws Exception {


            given(memberService.remove(any(), any(), any()))
                    .willReturn("삭제되었습니다.");

            MemberRemoveRequest memberRemoveRequest = new MemberRemoveRequest("testobjectidtestobject12", "geunho");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberRemoveRequest);

            mockMvc.perform(delete("/api/v1/members/testobjectidtestobject12")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))

                    .andExpect(status().is3xxRedirection())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string("삭제되었습니다."))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @DisplayName("해당 유저 정보가 없는 경우")
        @Test
        void invalidRemove2xx() throws Exception {

            given(memberService.remove(any(), any(), any()))
                    .willThrow(new BizException(MemberExceptionType.DELETE_NOT_FOUND_USER, "test"));

            MemberRemoveRequest memberRemoveRequest = new MemberRemoveRequest("testobjectidtestobject12", "geunho");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberRemoveRequest);

            mockMvc.perform(delete("/api/v1/members/testobjectidtestobject12")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))

                    .andExpect(status().is3xxRedirection())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(header().string("Location", "test"))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.DELETE_NOT_FOUND_USER.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.DELETE_NOT_FOUND_USER.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @DisplayName("비밀번호가 불일치할 경우")
        @Test
        void invalidRemove4xx() throws Exception {

            given(memberService.remove(any(), any(), any()))
                    .willThrow(new BizException(MemberExceptionType.WRONG_PASSWORD));

            MemberRemoveRequest memberRemoveRequest = new MemberRemoveRequest("testobjectidtestobject12", "geunho");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberRemoveRequest);

            mockMvc.perform(delete("/api/v1/members/testobjectidtestobject12")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.WRONG_PASSWORD.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.WRONG_PASSWORD.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @DisplayName("사용자 아이디가 입력되지 않은 경우")
        @Test
        void invalidRemoveMemberNotFoundUserId() throws Exception {
            //when
            MemberRemoveRequest memberRemoveRequest = new MemberRemoveRequest("", "geunho");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberRemoveRequest);

            mockMvc.perform(delete("/api/v1/members/testobjectidtestobject12")
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
            MemberRemoveRequest memberRemoveRequest = new MemberRemoveRequest("testobjectidtestobject12", "");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberRemoveRequest);

            mockMvc.perform(delete("/api/v1/members/testobjectidtestobject12")
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
        @DisplayName("정상인 경우")
        @Test
        void isMyPw() throws Exception {

            given(memberService.isMyPassword(any(), any()))
                    .willReturn("비밀번호가 일치합니다.");

            MemberCheckPasswordRequest memberCheckPasswordRequest = new MemberCheckPasswordRequest("password");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberCheckPasswordRequest);

            mockMvc.perform(post("/api/v1/members/guneho/password")
                            .with(csrf())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string("비밀번호가 일치합니다."))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @DisplayName("비밀번호를 입력하지 않은 경우")
        @Test
        void invalidPasswordIsMyPasswordNotFoundPassword() throws Exception {
            MemberCheckPasswordRequest memberRemoveRequest = new MemberCheckPasswordRequest("");
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

        @DisplayName("해당 유저 정보가 없는 경우")
        @Test
        void invalidUserIsMyPassword() throws Exception {

            given(memberService.isMyPassword(any(), any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            MemberCheckPasswordRequest memberRemoveRequest = new MemberCheckPasswordRequest("password");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberRemoveRequest);

            mockMvc.perform(post("/api/v1/members/geunho/password")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @DisplayName("비밀번호가 불일치할 경우")
        @Test
        void invalidPasswordIsMyPassword() throws Exception {

            given(memberService.isMyPassword(any(), any()))
                    .willThrow(new BizException(MemberExceptionType.WRONG_PASSWORD));

            MemberCheckPasswordRequest memberRemoveRequest = new MemberCheckPasswordRequest("password");
            String jsonString = JsonUtil.objectMapper.writeValueAsString(memberRemoveRequest);

            mockMvc.perform(post("/api/v1/members/geunho/password")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.WRONG_PASSWORD.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.WRONG_PASSWORD.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

    }

    @Nested
    @DisplayName("Logout Controller")
    class Logout {
        @Test
        @DisplayName("정상인 경우")
        void logout() throws Exception {

            given(memberService.logout(any(), any()))
                    .willReturn("로그아웃 되었습니다.");

            mockMvc.perform(post("/api/v1/members/logout")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("로그아웃 되었습니다."))
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andDo(MockMvcResultHandlers.print(System.out));

        }


        @DisplayName("토큰이 없는 경우")
        @Test
        void invalidLogout() throws Exception {

            given(memberService.logout(any(), any()))
                    .willThrow(new BizException(JwtExceptionType.LOGOUT_EMPTY_TOKEN, "test"));

            mockMvc.perform(post("/api/v1/members/logout")
                            .with(csrf()))

                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "test"))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(JwtExceptionType.LOGOUT_EMPTY_TOKEN.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(JwtExceptionType.LOGOUT_EMPTY_TOKEN.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }


    @Nested
    @DisplayName("Kakao Login Controller")
    class KakaoLogin {
        @Test
        @DisplayName("정상인 경우")
        void kakaoLogin() throws Exception {
            //given
            // Redirect 주소 설정
            URI redirectUri = new URI("test");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("accessToken", "accessToken");
            httpHeaders.setLocation(redirectUri);

            given(kaKaoMemberService.getAccessToken(any()))
                    .willReturn("code");

            given(kaKaoMemberService.getKakaoUserInfo(any()))
                    .willReturn(KakaoProfile.builder()
                            .id("geunho")
                            .properties(new KakaoProfile.Properties("geunho", "profileImage", "thumbnailImage"))
                            .connectedAt("geunho")
                            .kakaoAccount(null)
                            .build());

            given(kaKaoMemberService.login(any(), any()))
                    .willReturn("로그인이 완료되었습니다.");

            //when
            mockMvc.perform(get("/api/v0/members/kakao-login")
                            .with(csrf())
                            .param("code", "code"))

                    //then
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "http://localhost:3000/diary/dashboard"))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }


    @Nested
    @DisplayName("Kakao Logout Controller")
    class KakaoLogout {
        @Test
        @DisplayName("정상인 경우")
        void kakaoLogout() throws Exception {

            //given
            given(kaKaoMemberService.logout(any(), any()))
                    .willReturn("로그아웃 되었습니다.");

            //when
            mockMvc.perform(get("/api/v1/members/kakao-logout")
                            .with(csrf()))
                    //then
                    .andExpect(status().is3xxRedirection())
                    .andExpect(content().string("로그아웃 되었습니다."))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("이미 로그아웃된 사용자일 경우")
        void alreadyKakaoLogout() throws Exception {

            //given
            given(kaKaoMemberService.logout(any(), any()))
                    .willThrow(new BizException(JwtExceptionType.LOGOUT_EMPTY_TOKEN, "test"));

            //when
            mockMvc.perform(get("/api/v1/members/kakao-logout")
                            .with(csrf()))
                    //then
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "test"))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(JwtExceptionType.LOGOUT_EMPTY_TOKEN.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(JwtExceptionType.LOGOUT_EMPTY_TOKEN.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }
}