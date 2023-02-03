package com.sweep.jaksim31.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweep.jaksim31.config.EmbeddedRedisConfig;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.enums.MemberExceptionType;
import com.sweep.jaksim31.enums.SuccessResponseType;
import com.sweep.jaksim31.service.impl.MemberServiceImpl;
import com.sweep.jaksim31.utils.JsonUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.servlet.http.Cookie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * packageName :  com.sweep.jaksim31.Integration
 * fileName : IntegrationMemberTest
 * author :  김주현
 * date : 2023-01-28
 * description : Member service 통합테스트
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-28              김주현             최초 생성
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ImportAutoConfiguration(EmbeddedRedisConfig.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class IntegrationMemberTest {
    private static final String LOGIN_ID = "loginId";
    private static  String username = "geunho";
    private static String profileImage = "profileImage";
    private static String password = "password";
    private static String userId = "";
    private static String accessToken = "";
    private static String refreshToken = "";
    private static Cookie atkCookie;
    private static Cookie rtkCookie;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberServiceImpl memberService;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeAll
    public void init() {
        memberRepository.deleteAll();
    }


    @Nested
    @DisplayName("통합 테스트 01. 회원가입/로그인 - MemberService - 로그아웃")
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class) // 메소드 순서 지정
    class userTest{

        @Test
        @DisplayName("[정상] 1.가입 확인_회원 가입 전")
        @Order(1)
        public void isMember() throws Exception {
            MemberCheckLoginIdRequest memberCheckLoginIdRequest = new MemberCheckLoginIdRequest(LOGIN_ID);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberCheckLoginIdRequest);
            // when
            mockMvc.perform(post("/api/v0/members")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v0/members"))
                            //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getMessage())));
        }
        @Test
        @DisplayName("[정상] 2.회원 가입")
        @Order(2)
        public void signUp() throws Exception {
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest(LOGIN_ID, password, username, profileImage);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberSaveRequest);
            // when
            MvcResult mvcResult = mockMvc.perform(post("/api/v0/members/register")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v0/members/register"))
            //then
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string(SuccessResponseType.SIGNUP_SUCCESS.getMessage()))
                    .andDo(MockMvcResultHandlers.print(System.out))
                    .andReturn();
        }

        @Test
        @DisplayName("[정상] 3.가입 확인_회원 가입 후")
        @Order(3)
        public void isMemberAfterSignUp() throws Exception {
            MemberCheckLoginIdRequest memberCheckLoginIdRequest = new MemberCheckLoginIdRequest(LOGIN_ID);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberCheckLoginIdRequest);
            // when
            mockMvc.perform(post("/api/v0/members")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v0/members"))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string(LOGIN_ID+ SuccessResponseType.IS_MEMBER_SUCCESS.getMessage()))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("[정상] 4-1.로그인")
        @Order(4)
        public void logIn() throws Exception {
            LoginRequest loginRequest = new LoginRequest(LOGIN_ID, password);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(loginRequest);
            // when
            MvcResult mvcResult = mockMvc.perform(post("/api/v0/members/login")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v0/members/login"))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andDo(MockMvcResultHandlers.print(System.out))
                    .andReturn();

            // Cookie 설정 확인
            MockHttpServletResponse response = mvcResult.getResponse();
            assertEquals(response.getCookie("isLogin").getValue(),"true");
            assertNotNull(response.getCookie("atk").getValue());
            assertNotNull(response.getCookie("rtk").getValue());

            // 다음 테스트를 위한 static value 설정
            userId = mvcResult.getResponse().getCookie("userId").getValue();
            refreshToken = mvcResult.getResponse().getCookie("rtk").getValue();
            accessToken = mvcResult.getResponse().getCookie("atk").getValue();
            atkCookie = new Cookie("atk", accessToken);
            rtkCookie = new Cookie("rtk", refreshToken);
        }
        @Test
        @DisplayName("[예외] 4-2.로그인_비밀번호가 불일치할 경우")
        @Order(4)
        public void invalidLogin() throws Exception {
            LoginRequest loginRequest = new LoginRequest(LOGIN_ID, "wrongPassword");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(loginRequest);
            // when
            mockMvc.perform(post("/api/v0/members/login")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v0/members/login"))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.WRONG_PASSWORD.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.WRONG_PASSWORD.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("[정상] 5. 사용자 정보 조회")
        @Order(5)
        public void getMyInfo() throws Exception {

            // when
            mockMvc.perform(get("/api/v1/members/" + userId)
                            .cookie(atkCookie,rtkCookie)
                            .servletPath("/api/v1/members/"+userId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId", Matchers.is(userId)))
                    .andExpect(jsonPath("$.loginId", Matchers.is(LOGIN_ID)))
                    .andExpect(jsonPath("$.username", Matchers.is(username)))
                    .andExpect(jsonPath("$.profileImage", Matchers.is(profileImage)))
                    .andExpect(jsonPath("$.diaryTotal", Matchers.is(0)))
                    .andDo(MockMvcResultHandlers.print(System.out));

        }

        @Test
        @DisplayName("[정상] 6-1. 사용자 정보 Update_Username, ProfileImage")
        @Order(6)
        public void updateMember() throws Exception {
            String newUsername = "방근호";
            String newProfile = "프로필이미지";
            MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest(newUsername, newProfile);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberUpdateRequest);

            // when
            mockMvc.perform(patch("/api/v1/members/"+userId)
                            .content(jsonRequest)
                            .cookie(atkCookie,rtkCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/members/"+userId))
                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out));

            // DB 확인
            Members members = memberRepository.findById(userId).get();
            assertEquals(members.getUsername(), newUsername);
            assertEquals(members.getProfileImage(), newProfile);
            username = newUsername;
            profileImage = newProfile;
        }

        @Test
        @DisplayName("[정상] 6-2. 사용자 정보 Update_Username")
        @Order(6)
        public void updateMember_Username() throws Exception {
            String newUsername = "Bang";
            MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest(newUsername, "");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberUpdateRequest);

            // when
            mockMvc.perform(patch("/api/v1/members/"+userId)
                            .content(jsonRequest)
                            .cookie(atkCookie,rtkCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/members/"+userId))
                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out));

            // DB 확인
            Members members = memberRepository.findById(userId).get();
            assertEquals(members.getUsername(), newUsername);
            assertEquals(members.getProfileImage(), profileImage);
            username = newUsername;
        }

        @Test
        @DisplayName("[정상] 6-3. 사용자 정보 Update_ProfileImage")
        @Order(6)
        public void updateMember_ProfileImage() throws Exception {
            String newProfile = "Bang_image";
            MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("", newProfile);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberUpdateRequest);

            // when
            mockMvc.perform(patch("/api/v1/members/"+userId)
                            .content(jsonRequest)
                            .cookie(atkCookie,rtkCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/members/"+userId))
                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out));

            // DB 확인
            Members members = memberRepository.findById(userId).get();
            assertEquals(members.getUsername(), username);
            assertEquals(members.getProfileImage(), newProfile);
            profileImage = newProfile;
        }

        @Test
        @DisplayName("[정상] 7-1. 비밀번호 변경_CheckPW")
        @Order(7)
        public void changePassword_checkPassword() throws Exception {
            MemberCheckPasswordRequest checkPasswordRequest = new MemberCheckPasswordRequest(password);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(checkPasswordRequest);

            // when
            mockMvc.perform(post("/api/v1/members/"+ LOGIN_ID +"/password")
                            .content(jsonRequest)
                            .cookie(atkCookie,rtkCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/members/"+ LOGIN_ID +"/password"))
                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[정상] 7-2. 비밀번호 변경_ChangePW")
        @Order(8)
        public void changePassword_changePassword() throws Exception {
            String newPassword = "newPassword";
            MemberUpdatePasswordRequest memberUpdatePasswordRequest = new MemberUpdatePasswordRequest(newPassword);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberUpdatePasswordRequest);

            // when
            mockMvc.perform(put("/api/v0/members/"+ LOGIN_ID +"/password")
                            .content(jsonRequest)
                            .cookie(atkCookie,rtkCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v0/members/"+ LOGIN_ID +"/password"))
                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out));
            password = newPassword;
        }
        @Test
        @DisplayName("[예외] 7-3. 비밀번호 변경_CheckPW with BeforePW")
        @Order(9)
        public void changePassword_beforePassword() throws Exception {
            MemberCheckPasswordRequest checkPasswordRequest = new MemberCheckPasswordRequest("password");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(checkPasswordRequest);

            // when
            mockMvc.perform(post("/api/v1/members/"+ LOGIN_ID +"/password")
                            .content(jsonRequest)
                            .cookie(atkCookie,rtkCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/members/"+ LOGIN_ID +"/password"))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.WRONG_PASSWORD.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.WRONG_PASSWORD.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("[정상] 7-4. 비밀번호 변경_CheckPW with AfterPW")
        @Order(9)
        public void changePassword_afterPassword() throws Exception {
            MemberCheckPasswordRequest checkPasswordRequest = new MemberCheckPasswordRequest(password);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(checkPasswordRequest);

            // when
            mockMvc.perform(post("/api/v1/members/"+ LOGIN_ID +"/password")
                            .content(jsonRequest)
                            .cookie(atkCookie,rtkCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/members/"+ LOGIN_ID +"/password"))
                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out));
        }


        @Test
        @DisplayName("[정상] last. 로그아웃")
        @Order(10)
        public void logout() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer "+refreshToken);

            // when
            MvcResult mvcResult = mockMvc.perform(post("/api/v1/members/logout")
                            .cookie(atkCookie,rtkCookie)
                            .servletPath("/api/v1/members/logout"))
                    //then
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print(System.out))
                    .andReturn();

            // Cookie 설정 확인
            MockHttpServletResponse response = mvcResult.getResponse();
            assertEquals(response.getCookie("isLogin").getValue(),"false");
            assertEquals(response.getCookie("atk").getValue(),"");
            assertEquals(response.getCookie("rtk").getValue(),"");
            assertEquals(response.getCookie("todayDiaryId").getValue(),"");
            assertEquals(response.getCookie("userId").getValue(),"");

            atkCookie = new Cookie("atk", response.getCookie("atk").getValue());
            rtkCookie = new Cookie("rtk", response.getCookie("rtk").getValue());
        }
    }
}