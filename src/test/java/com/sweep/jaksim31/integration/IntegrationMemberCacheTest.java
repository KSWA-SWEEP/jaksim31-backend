package com.sweep.jaksim31.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweep.jaksim31.adapter.cache.MemberCacheAdapter;
import com.sweep.jaksim31.adapter.cache.RefreshTokenCacheAdapter;
import com.sweep.jaksim31.auth.TokenProvider;
import com.sweep.jaksim31.config.EmbeddedRedisConfig;
import com.sweep.jaksim31.domain.auth.Authority;
import com.sweep.jaksim31.domain.auth.MemberAuth;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.MemberInfoResponse;
import com.sweep.jaksim31.dto.member.MemberRemoveRequest;
import com.sweep.jaksim31.dto.member.MemberSaveRequest;
import com.sweep.jaksim31.dto.member.MemberUpdateRequest;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.service.impl.MemberServiceImpl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ImportAutoConfiguration(EmbeddedRedisConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class IntegrationMemberCacheTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberServiceImpl memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberCacheAdapter memberCacheAdapter;
    @Autowired
    private RefreshTokenCacheAdapter refreshTokenCacheAdapter;

    private static final String memberCachePrefix = "memberCache::";
    private static final String loginId = "loginId";
    private static final String password = "password";
    private static final String invalidPassword = "asdasdsadadad";
    private static final String username = "username";
    private static final String profile = "profileImage";
    private static String userId;
    private static final String invalidUserId = "adasdadasfa44";
    private static String accessToken;
    private static String refreshToken;
    private static Members members;

    private static MemberUpdateRequest memberUpdateRequest;
    private static MemberRemoveRequest memberRemoveRequest;
    private static MemberInfoResponse memberInfoResponse;

    private static final MockHttpServletRequest request = new MockHttpServletRequest();
    private static final MockHttpServletResponse response = new MockHttpServletResponse();


    @BeforeAll
    public void setUp() {
        // 테스트용으로 member entity 생성 후 db에 저장
        members = new MemberSaveRequest(loginId, password, username, profile)
                .toMember(passwordEncoder, false);

        memberRepository.save(members);

        // db에 저장된 데이터로 테스트용 데이터로 생성
        userId = Objects.requireNonNull(memberRepository.findByLoginId(loginId).orElse(null)).getId();
        memberUpdateRequest = new MemberUpdateRequest("newUsername", "newProfileImage");
        memberInfoResponse = MemberInfoResponse.of(Objects.requireNonNull(memberRepository.findByLoginId(loginId).orElse(null)));
        memberRemoveRequest = MemberRemoveRequest.builder().userId(userId).password(password).build();

        // 테스트용 token 생성
        Set<Authority> authoritySet = new HashSet<>();
        authoritySet.add(new Authority(loginId, MemberAuth.of("ROLE_USER")));
        accessToken = tokenProvider.createAccessToken(loginId, authoritySet);
        refreshToken = tokenProvider.createRefreshToken(loginId, authoritySet);

        // 테스트용 MockHttpServletRequest 설정
        request.setCookies(new Cookie("atk", accessToken), new Cookie("rtk", refreshToken));
    }

    @Nested
    @DisplayName("통합 테스트 01. 유저 정보 캐싱 테스트")
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class) // 메소드 순서 지정
    class memberCachingTest {

        @Test
        @DisplayName("[정상] 유저 정보 조회 시 캐싱 ")
        @Order(1)
        public void getMyInfoSearch() {

            // when
            MemberInfoResponse result = memberService.getMyInfo(userId, request);
            MemberInfoResponse cacheResult = memberCacheAdapter.get(memberCachePrefix + userId);

            //then
            assertNotNull(cacheResult);
            assertEquals(result.getUserId(), cacheResult.getUserId());
            assertEquals(result.getLoginId(), cacheResult.getLoginId());
        }

        @Test
        @DisplayName("[예외] 유저 정보 조회 시 예외 발생하면 캐싱 X")
        @Order(2)
        public void failGetMyInfoSearch() {
            // when
            try {
                MemberInfoResponse result = memberService.getMyInfo(invalidUserId, request);
            } catch (BizException ex) {
                MemberInfoResponse cacheResult = memberCacheAdapter.get(memberCachePrefix + invalidUserId);
                //then
                assertNull(cacheResult);
            }
        }

        @Test
        @DisplayName("[정상] 유저 정보 변경 시 캐시 데이터 삭제")
        @Order(3)
        public void updateUserInfo() {
            // when
            memberService.updateMemberInfo(userId, memberUpdateRequest, request);
            MemberInfoResponse cacheResult = memberCacheAdapter.get(memberCachePrefix + userId);

            //then
            assertNull(cacheResult);
        }

        @Test
        @DisplayName("[예외] 유저 정보 변경 시 예외 발생하면 캐시 데이터 삭제 X")
        @Order(4)
        public void failUpdateUserInfo() {
            // given
            memberCacheAdapter.put(memberCachePrefix + userId, memberInfoResponse);
            // when
            try {
                memberService.updateMemberInfo(invalidUserId, memberUpdateRequest, request);
            } catch (BizException ex) {
                MemberInfoResponse cacheResult = memberCacheAdapter.get(memberCachePrefix + userId);
                //then
                assertEquals(cacheResult.getUserId(), userId);
            }
        }

        @Test
        @DisplayName("[정상] 회원탈퇴 시 캐시 데이터 삭제")
        @Order(5)
        public void deleteUserInfo() throws URISyntaxException {
            // when
            memberService.remove(userId, memberRemoveRequest, response, request);
            MemberInfoResponse cacheResult = memberCacheAdapter.get(memberCachePrefix + userId);

            //then
            assertNull(cacheResult);
        }

        @Test
        @DisplayName("[예외] 회원탈퇴 시 예외 발생하면 캐시 데이터 삭제 X")
        @Order(6)
        public void failDeleteUserInfo() {
            // given
            memberCacheAdapter.put(memberCachePrefix + userId, memberInfoResponse);
            // when
            try {
                memberService.remove(invalidUserId, memberRemoveRequest, response, request);
            } catch (BizException ex) {
                MemberInfoResponse cacheResult = memberCacheAdapter.get(memberCachePrefix + userId);
                //then
                assertEquals(cacheResult.getUserId(), userId);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("통합 테스트 02. Refresh Token 캐싱 테스트")
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class) // 메소드 순서 지정
    class refreshTokenCachingTest{

        @Test
        @DisplayName("[정상] 로그인 시 캐싱 ")
        @Order(1)
        public void successLogin() {
            // given
            memberRepository.save(members);
            LoginRequest loginRequest = LoginRequest.builder().loginId(loginId).password(password).build();

            // when
            memberService.login(loginRequest, response);
            String cacheResult = refreshTokenCacheAdapter.get(loginId);

            //then
            assertNotNull(cacheResult);
        }

        @Test
        @DisplayName("[예외] 로그인 시 예외 발생하면 캐싱 X")
        @Order(2)
        public void failLogin() {
            //given
            refreshTokenCacheAdapter.delete(loginId);
            LoginRequest invalidLoginRequest = LoginRequest.builder().loginId(loginId).password(invalidPassword).build();

            // when
            try {
                memberService.login(invalidLoginRequest, response);
            } catch (BizException ex) {
                String cacheResult = refreshTokenCacheAdapter.get(loginId);
                //then
                assertNull(cacheResult);
            }
        }

        /**
         * TODO
         * 토큰이 변경이 안된다가 아니라 새로 만들어지는데 토큰이 그대로임;;
         */
        @Test
        @DisplayName("[정상] 토큰 재발급 시 캐시 토큰 수정")
        @Order(3)
        public void successReissue() throws InterruptedException {

            //given
            LoginRequest loginRequest = LoginRequest.builder().loginId(loginId).password(password).build();
            memberService.login(loginRequest, response);
            Thread.sleep(300);

            String oldToken = refreshTokenCacheAdapter.get(loginId);
            request.setCookies(new Cookie("atk", accessToken), new Cookie("rtk", oldToken));

            // when
            memberService.reissue(request, response);

            // then
            assertNotEquals(oldToken, refreshTokenCacheAdapter.get(loginId));
        }

        @Test
        @DisplayName("[정상] 로그아웃 시 캐시 데이터 삭제")
        @Order(4)
        public void successLogout() {
            // when
            memberService.logout(request, response);
            String token = refreshTokenCacheAdapter.get(loginId);

            // then
            assertNull(token);
        }

//        @Test
//        @DisplayName("[정상] 2.회원 가입")
//        @Order(2)
//        public void signUp() throws Exception {
//            MemberSaveRequest memberSaveRequest = new MemberSaveRequest(loginId, password, username, profile);
//            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(memberSaveRequest);
//            // when
//            MvcResult mvcResult = mockMvc.perform(post("/api/v0/members/register")
//                            .content(jsonRequest)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .servletPath("/api/v0/members/register"))
//                    //then
//                    .andExpect(status().isCreated())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("$.userId", Matchers.is(notNullValue())))
//                    .andDo(MockMvcResultHandlers.print(System.out))
//                    .andReturn();
//            System.out.println("####### " + mvcResult.getResponse().getContentAsString());
//            JSONParser parser = new JSONParser();
//            JSONObject jsonObject = (JSONObject) parser.parse(mvcResult.getResponse().getContentAsString());
//
//            Members members = memberRepository.findById(jsonObject.getAsString("userId")).get();
//            assertEquals(members.getLoginId(), loginId);
//            assertEquals(members.getUsername(), username);
//            assertEquals(members.getProfileImage(), profile);
//        }

    }
}
