package com.sweep.jaksim31.service.impl;

import com.sweep.jaksim31.adapter.cache.RefreshTokenCacheAdapter;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.dto.member.*;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.MemberExceptionType;
import com.sweep.jaksim31.utils.CookieUtil;
import com.sweep.jaksim31.utils.RedirectionUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * packageName :  com.sweep.jaksim31.service.impl
 * fileName : MemberServiceImplTest
 * author :  방근호
 * date : 2023-01-17
 * description : Member Service Test
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-17           방근호             최초 생성
 */

@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "usernmae", password = "password", roles = "ROLE_USER")
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private DiaryRepository diaryRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RedirectionUtil redirectionUtil;
    @Mock
    private RefreshTokenCacheAdapter refreshTokenCacheAdapter;
    private static MockedStatic<MemberSaveResponse> memberSaveResponse;
    private static MockedStatic<MemberInfoResponse> memberInfoResponse;
    private static MockedStatic<CookieUtil> cookieUtil;
    private static MemberSaveRequest memberSaveRequest;
    private  static String fakeMemberId;




    @BeforeAll
    public static void setup(){
        memberSaveResponse = mockStatic(MemberSaveResponse.class);
        memberInfoResponse = mockStatic(MemberInfoResponse.class);
        cookieUtil = mockStatic(CookieUtil.class);
        fakeMemberId = "abcedfg";
        memberSaveRequest = MemberSaveRequest.builder()
                .username("username123")
                .password("password")
                .loginId(fakeMemberId)
                .profileImage("profileImage")
                .build();
    }

    @AfterAll
    public static void finish(){
        memberSaveResponse.close();
        memberInfoResponse.close();
    }

    @Nested
    @DisplayName("회원가입 서비스")
    class Signup{
        @Test
        @DisplayName("정상인 경우")
        void signup() {
            //given
            Members members = memberSaveRequest.toMember(passwordEncoder, false);
            MemberSaveResponse memberSaveResponse = new MemberSaveResponse("userid", fakeMemberId, "username123", "profileImage");
            assert memberSaveRequest.toMember(passwordEncoder, false) != null;
            given(memberRepository.existsByLoginId(any()))
                    .willReturn(false);

            given(memberRepository.save(any()))
                    .willReturn(members);

            given(MemberSaveResponse.of(any()))
                    .willReturn(memberSaveResponse);

            //when
            MemberSaveResponse expected = memberService.signup(memberSaveRequest);

            //then
            assert expected != null;
            assertEquals(expected.getLoginId(), memberSaveRequest.getLoginId());
            assertEquals(expected.getUsername(), memberSaveRequest.getUsername());
            assertEquals(expected.getProfileImage(), memberSaveRequest.getProfileImage());
            verify(memberRepository, times(1)).existsByLoginId(any());

        }

        @Test
        @DisplayName("실패한 경우 - 아이디 중복, DB에 저장 X")
        void invalidSignup() {
            //given
            String fakeMemberId = "abcedfg";
            MemberSaveRequest memberSaveRequest = MemberSaveRequest.builder()
                    .username("username123")
                    .password("password")
                    .loginId(fakeMemberId)
                    .profileImage("profileImage")
                    .build();
            

            assert memberSaveRequest.toMember(passwordEncoder, false) != null;
            given(memberRepository.existsByLoginId(any()))
                    .willThrow(new BizException(MemberExceptionType.DUPLICATE_USER));

            assertThrows(BizException.class, () -> memberService.signup(memberSaveRequest));
            verify(memberRepository, never()).save(any());

        }
    }

    @Nested
    @DisplayName("멤버 검증 서비스")
    class IsMember {
        @Test
        @DisplayName("정상인 경우 - 회원인 경우")
        void isMember() {
            //given
            String loginId = "loginId";
            MemberCheckLoginIdRequest memberCheckLoginIdRequest = new MemberCheckLoginIdRequest(loginId);
            Members members = memberSaveRequest.toMember(passwordEncoder, false);
            given(memberRepository.existsByLoginId(any()))
                    .willReturn(true);

            //when
            String res = memberService.isMember(memberCheckLoginIdRequest);

            //then
            assertEquals(loginId+" 해당 이메일은 가입하였습니다.", res);
            verify(memberRepository, times(1)).existsByLoginId(loginId);

        }
        @Test
        @DisplayName("실패한 경우 - 회원이 아닌 경우")
        void isNotMember() {
            //given
            String loginId = "loginId";
            MemberCheckLoginIdRequest memberCheckLoginIdRequest = new MemberCheckLoginIdRequest(loginId);
            given(memberRepository.existsByLoginId(any()))
                    .willReturn(false);

            assertThrows(BizException.class, () -> memberService.isMember(memberCheckLoginIdRequest));
        }

    }

    @Nested
    @DisplayName("비밀번호 변경 서비스")
    class UpdatePassword {

        @Test
        @DisplayName("정상인 경우")
        void success() {
            //given
            given(passwordEncoder.encode(any()))
                    .willReturn("password");

            Members members = memberSaveRequest.toMember(passwordEncoder, false);
            MemberUpdatePasswordRequest memberUpdatePasswordRequest = new MemberUpdatePasswordRequest("password");

            given(memberRepository.findByLoginId(fakeMemberId))
                    .willReturn(Optional.of(members));

            given(memberRepository.save(members))
                    .willReturn(members);

            //when
            String res = memberService.updatePassword(fakeMemberId, memberUpdatePasswordRequest);

            //then
            assertEquals(res, "회원 정보가 정상적으로 변경되었습니다.");
            verify(memberRepository, times(1)).findByLoginId(fakeMemberId);
            verify(memberRepository, times(1)).save(members);
        }

        @Test
        @DisplayName("실패한 경우 - 회원이 아닐 경우")
        void failure() {
            //given
            given(passwordEncoder.encode(any()))
                    .willReturn("password");

            Members members = memberSaveRequest.toMember(passwordEncoder, false);
            MemberUpdatePasswordRequest memberUpdatePasswordRequest = new MemberUpdatePasswordRequest("password");

            given(memberRepository.findByLoginId(fakeMemberId))
                    .willThrow( new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when & then
            assertThrows(BizException.class, () -> memberService.updatePassword(fakeMemberId, memberUpdatePasswordRequest));
            verify(memberRepository, times(1)).findByLoginId(fakeMemberId);
            verify(memberRepository, never()).save(members); // save 메소드는 호출 되지 않을 것이다.
        }
    }


    @Nested
    @DisplayName("오브젝트 아이디 멤버 정보 조회 서비스")
    class GetMemberInfo {

        @Test
        @DisplayName("정상인 경우")
        void getMyInfo() {
            String userId = "63c4f6cbeb0a310a89188df6";
            Members members = memberSaveRequest.toMember(passwordEncoder, false);
            MemberInfoResponse memberInfoResponse1 = new MemberInfoResponse(userId, "loginId", "username", "profileImage", null, 10);
            //given

            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(members));

            given(MemberInfoResponse.of(members))
                    .willReturn(memberInfoResponse1);

            //when
            MemberInfoResponse res = memberService.getMyInfo(userId);
            //then
            verify(memberRepository).findById(userId);
            assert res != null;
            assertEquals(res.getUserId(), userId);
        }

        @Test
        @DisplayName("실패한 경우 - 회원이 아닌 경우")
        void invalidGetMyInfo() {
            String userId = "63c4f6cbeb0a310a89188df6";

            //given
            given(memberRepository.findById(userId))
                    .willReturn(Optional.empty());

            //when & then
            assertThrows(BizException.class, () -> memberService.getMyInfo(userId));
            verify(memberRepository, times(1)).findById(userId);

        }
    }
//    @Nested
//    @DisplayName("로그인 아이디 멤버 정보 조회 서비스")
//    class GetMemberInfoByLoginId {
//        String loginId = "geunho";
//
//        @Test
//        @DisplayName("정상인 경우 - 멤버, 일기 쿼리 각 1개")
//        void success() {
//            //given
//            given(passwordEncoder.encode(any()))
//                    .willReturn("password");
//
//            Members members = memberSaveRequest.toMember(passwordEncoder, false);
//            MemberInfoResponse memberInfoResponse1 = new MemberInfoResponse(loginId, fakeMemberId, "username", "profileImage", null, 10);
//
//            given(memberRepository.findByLoginId(loginId))
//                    .willReturn(Optional.of(members));
//
//            given(diaryRepository.findDiaryByUserIdAndDate(any(), any()))
//                    .willReturn(Optional.empty());
//
//            given(MemberInfoResponse.of(members))
//                    .willReturn(memberInfoResponse1);
//
//            //when
//            MemberInfoResponse res = memberService.getMyInfoByLoginId(loginId, any());
//
//            //then
//            verify(memberRepository, times(1)).findByLoginId(loginId);
//            verify(diaryRepository, times(1)).findDiaryByUserIdAndDate(any(), any());
//            assert res != null;
//            assertEquals(res.getLoginId(), members.getLoginId());
//        }
//
//        @Test
//        @DisplayName("실패한 경우 - 유저가 없을 경우")
//        void fail() {
//            //given
//
//            given(memberRepository.findByLoginId(loginId))
//                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));
//
//            //when & then
//            assertThrows(BizException.class, () -> memberService.getMyInfoByLoginId(loginId, any()));
//            // diaryRepository method는 호출되지 않을 것
//            verify(diaryRepository, never()).findDiaryByUserIdAndDate(any(), any());
//        }
//    }

    @Nested
    @DisplayName("멤버 정보 업데이트 서비스")
    class UpdateMember {

        @DisplayName("정상인 경우 - 조회, 저장 쿼리 각 1개 씩")
        @Test
        void success() {
            //given
            String userId = "63c4f6cbeb0a310a89188df6";
            given(passwordEncoder.encode(any()))
                    .willReturn("password");

            Members members = memberSaveRequest.toMember(passwordEncoder, false);
            MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("username", "profileImage");

            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(members));

            given(memberRepository.save(members))
                    .willReturn(members);

            //when
            String res = memberService.updateMemberInfo(userId, memberUpdateRequest);

            //then
            assertEquals(res, "회원 정보가 정상적으로 변경되었습니다.");
            verify(memberRepository, times(1)).findById(userId);
            verify(memberRepository, times(1)).save(members);
        }

        @DisplayName("실패한 경우 - 등록된 회원이 아닌 경우")
        @Test
        void fail() {
            //given
            MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("username", "profileImage");
            String userId = "63c4f6cbeb0a310a89188df6";
            Members members = memberSaveRequest.toMember(passwordEncoder, false);
            given(memberRepository.findById(userId))
                    .willReturn(Optional.empty());

            //when & then
            assertThrows(BizException.class, ()-> memberService.updateMemberInfo(userId, memberUpdateRequest));
            verify(memberRepository, times(1)).findById(userId);
            verify(memberRepository, never()).save(members);
        }

    }

    @Nested
    @DisplayName("비밀번호 검증 서비스")
    class IsMyPassword {

        @Test
        @DisplayName("정상인 경우 - 회원 존재, 패스워드 일치")
        void success() {
            // given
            given(passwordEncoder.encode(any()))
                    .willReturn("password");

            Members members = memberSaveRequest.toMember(passwordEncoder, false);

            given(memberRepository.findByLoginId(fakeMemberId))
                    .willReturn(Optional.of(members));

            MemberCheckPasswordRequest memberCheckPasswordRequest = new MemberCheckPasswordRequest("password");

            given(passwordEncoder.matches(any(), any()))
                    .willReturn(true);

            // when
            String res = memberService.isMyPassword(fakeMemberId, memberCheckPasswordRequest);

            //then
            assertEquals(res, "비밀번호가 일치합니다.");
            verify(memberRepository, times(1)).findByLoginId(fakeMemberId);
            verify(passwordEncoder, times(1)).encode(any());
            verify(passwordEncoder, times(1)).matches(any(), any());

        }

        @Test
        @DisplayName("실패한 경우 - 회원 존재 X")
        void failure() {
            // given
            given(memberRepository.findByLoginId(fakeMemberId))
                    .willReturn(Optional.empty());

            MemberCheckPasswordRequest memberCheckPasswordRequest = new MemberCheckPasswordRequest("password");

            //when & then
            assertThrows(BizException.class, () -> memberService.isMyPassword(fakeMemberId, memberCheckPasswordRequest));
        }

        @Test
        @DisplayName("실패한 경우 - 비밀번호 불일치")
        void invalidPassword() {
            // given
            given(passwordEncoder.encode(any()))
                    .willReturn("password");

            Members members = memberSaveRequest.toMember(passwordEncoder, false);

            given(memberRepository.findByLoginId(fakeMemberId))
                    .willReturn(Optional.of(members));

            MemberCheckPasswordRequest memberCheckPasswordRequest = new MemberCheckPasswordRequest("password");

            given(passwordEncoder.matches(any(), any()))
                    .willReturn(false);

            // when & then
            assertThrows(BizException.class, () -> memberService.isMyPassword(fakeMemberId, memberCheckPasswordRequest));
            verify(memberRepository, times(1)).findByLoginId(fakeMemberId);
            verify(passwordEncoder, times(1)).encode(any());
            verify(passwordEncoder, times(1)).matches(any(), any());
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 서비스")
    class RemoveMember {
        String userId = "63c4f6cbeb0a310a89188df6";

        @Test
        @DisplayName("정상인 경우 - 회원 존재, 패스워드 일치")
        void success() throws URISyntaxException {
            // given
            given(passwordEncoder.encode(any()))
                    .willReturn("password");

            Members members = memberSaveRequest.toMember(passwordEncoder, false);
            MemberRemoveRequest memberRemoveRequest = new MemberRemoveRequest(userId, "password");

            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(members));

            given(passwordEncoder.matches(any(), any()))
                    .willReturn(true);

            given(memberRepository.save(any()))
                    .willReturn(members);

            // 아무것도 안하게 하겠음
            doNothing().when(refreshTokenCacheAdapter).delete(any());

            // when
            String res = memberService.remove(userId, memberRemoveRequest,any());

            //then
            assertEquals(res, "정상적으로 회원탈퇴 작업이 처리되었습니다.");
            verify(memberRepository, times(1)).findById(userId);
            verify(passwordEncoder, times(1)).encode(any());
            verify(passwordEncoder, times(1)).matches(any(), any());
            verify(refreshTokenCacheAdapter, times(1)).delete(any());

        }

        @Test
        @DisplayName("실패한 경우 - 회원 존재 X")
        void failure() {
            // given
            given(memberRepository.findById(userId))
                    .willReturn(Optional.empty());

            MemberRemoveRequest memberRemoveRequest = new MemberRemoveRequest(userId, "password");

            //when & then
            assertThrows(BizException.class, () -> memberService.remove(userId, memberRemoveRequest,any()));
        }

        @Test
        @DisplayName("실패한 경우 - 비밀번호 불일치")
        void invalidPassword() {
            // given
            given(passwordEncoder.encode(any()))
                    .willReturn("password");

            Members members = memberSaveRequest.toMember(passwordEncoder, false);

            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(members));

            given(passwordEncoder.matches(any(), any()))
                    .willReturn(true);

            MemberRemoveRequest memberRemoveRequest = new MemberRemoveRequest(userId, "password");

            given(passwordEncoder.matches(any(), any()))
                    .willReturn(false);

            // when & then
            assertThrows(BizException.class, () -> memberService.remove(userId, memberRemoveRequest,any()));
            verify(memberRepository, times(1)).findById(userId);
            verify(passwordEncoder, times(1)).encode(any());
            verify(passwordEncoder, times(1)).matches(any(), any());
        }
    }


//    @Nested
//    @DisplayName("로그인 서비스")
//    class Login {
//        @Test
//        void login() {
//            //given
//            LoginRequest loginRequest = new LoginRequest("loginId", "password");
//
//            String accessToken = "accessToken";
//
//            given(passwordEncoder.encode(any()))
//                    .willReturn("password");
//
//            Members members = memberSaveRequest.toMember(passwordEncoder, false);
//            System.out.println(members);
//
//            given(tokenProvider.createTokenDTO(any(), any(), any(), any()))
//                    .willReturn(TokenResponse.builder()
//                            .accessToken("accessToken")
//                            .refreshToken("refreshToken")
//                            .loginId("loginId")
//                            .expTime("100000")
//                            .build());
//
//            given(memberRepository.findByLoginId(any()))
//                    .willReturn(Optional.ofNullable(members));
//
//            setUserToContextByUsername("abcedfg");
//
//            given(authenticationManager.authenticate(any()))
//                    .willReturn(SecurityContextHolder.getContext().getAuthentication());
//
//            given(tokenProvider.createAccessToken(any(), any()))
//                    .willReturn(accessToken);
//
//            given(tokenProvider.createRefreshToken(any(), any()))
//                    .willReturn(accessToken);
//
//            given(customUserDetailsService.getMember(any()))
//                    .willReturn(members);
//
//            ResponseEntity<TokenResponse> res = memberService.login(loginRequest, any());
//
//            System.out.println(res.getBody());
//        }
//        private void setUserToContextByUsername(String username) {
//            CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(memberRepository);
//            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//            SecurityContext context = SecurityContextHolder.getContext();
//            context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));
//        }
//    }
}