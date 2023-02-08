package com.sweep.jaksim31.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweep.jaksim31.config.EmbeddedRedisConfig;
import com.sweep.jaksim31.domain.diary.Diary;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.dto.diary.DiaryAnalysisRequest;
import com.sweep.jaksim31.dto.diary.DiarySaveRequest;
import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.dto.member.MemberRemoveRequest;
import com.sweep.jaksim31.dto.member.MemberSaveRequest;
import com.sweep.jaksim31.enums.DiaryExceptionType;
import com.sweep.jaksim31.enums.MemberExceptionType;
import com.sweep.jaksim31.enums.SuccessResponseType;
import com.sweep.jaksim31.service.impl.MemberServiceImpl;
import com.sweep.jaksim31.utils.JsonUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * packageName :  com.sweep.jaksim31.integration
 * fileName : IntegrationDiaryTest
 * author :  김주현
 * date : 2023-01-31
 * description : Diary service 통합테스트
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-31              김주현             최초 생성
 * 2023-02-01              김주현             사용자 정보 recentDiary 테스트 추가
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ImportAutoConfiguration(EmbeddedRedisConfig.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class IntegrationDiaryTest {
    private static final String LOGIN_ID = "kjh@test.com";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "kjh";
    private static final String PROFILE_IMAGE = "profileImage";
    private static final String SENTENCE = "나는 이제 경, 못 언덕 피어나듯이 버리었습니다. 이 별을 오는 다하지 계절이 겨울이 다 부끄러운 봅니다. 위에 멀듯이, 이국 어머님, 가난한 별에도 책상을 봅니다. 언덕 어머님, 아스라히 까닭입니다. 나는 자랑처럼 경, 버리었습니다. 못 별이 소학교 프랑시스 사랑과 가을 까닭입니다. 때 오는 없이 거외다. 남은 위에 오는 별 계십니다. 그리워 불러 부끄러운 같이 거외다. 한 봄이 그러나 이웃 헤일 봅니다. 비둘기, 별들을 사랑과 벌써 듯합니다.";
    private static final String NEW_EMOTION = "newEmotion";
    private static String userId = "";
    private static String accessToken = "";
    private static String refreshToken = "";
    private static Cookie atkCookie;
    private static Cookie rtkCookie;
    private static String diaryId;
    private static String recentDiaryId;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberServiceImpl memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    DiaryRepository diaryRepository;

    @BeforeAll
    public void init() {
        memberRepository.deleteAll();
        diaryRepository.deleteAll();
    }


    public DiarySaveRequest getDiaryRequest(int num, String userId) {
        return DiarySaveRequest.builder()
                .userId(userId)
                .content("content" + num)
                .date(LocalDate.of(2023, 1, num))
                .emotion(Integer.toString(num%9+1))
                .keywords(new String[]{"keyword" + num})
                .thumbnail("thumbnail" + num)
                .build();
    }

    @Nested
    @DisplayName("통합 테스트 02. 회원가입/로그인 - DiaryService - 회원탈퇴")
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class) // 메소드 순서 지정
    class diaryTest{
        @Test
        @DisplayName("[정상] 1.회원 가입")
        @Order(1)
        void signUp() throws Exception {
            MemberSaveRequest memberSaveRequest = new MemberSaveRequest(LOGIN_ID, PASSWORD, USERNAME, PROFILE_IMAGE);
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
        @DisplayName("[정상] 2.로그인")
        @Order(2)
        void logIn() throws Exception {
            LoginRequest loginRequest = new LoginRequest(LOGIN_ID, PASSWORD);
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
            assertEquals("true", response.getCookie("isLogin").getValue());
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
        @DisplayName("[정상] 3.사용자 일기 작성")
        @Order(3)
        void saveDiaries() throws Exception {
            // when
            Members members = Members.builder().build();
            // Default Diary setting
            for (int i = 1; i <= 20; i++) {
                DiarySaveRequest request = getDiaryRequest(i, userId);
                String jsonRequest = JsonUtil.objectMapper.writeValueAsString(request);

                mockMvc.perform(post("/api/v1/diaries")
                                .cookie(atkCookie,rtkCookie)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                                .servletPath("/api/v1/diaries"))
                        //then
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType("text/plain;charset=UTF-8"))
                        .andExpect(content().string(SuccessResponseType.DIARY_SAVE_SUCCESS.getMessage()))
                        .andDo(MockMvcResultHandlers.print(System.out));

                // Member DB 확인
                members = memberRepository.findById(userId).get();
                assertEquals(members.getDiaryTotal(), i);
                assertEquals(members.getRecentDiary().getDiaryDate(), LocalDate.of(2023, 1, i));
            }
            assertEquals(members.getRecentDiary().getDiaryDate(), LocalDate.of(2023, 1, 20));
            recentDiaryId = members.getRecentDiary().getDiaryId();
        }

        @Test
        @DisplayName("[정상] 4-1.사용자 일기 조회_전체")
        @Order(4)
        void findUserDiary_All() throws Exception {
            Members members = memberRepository.findById(userId).get();

            // when
            MvcResult mvcResult = mockMvc.perform(get("/api/v1/diaries/"+userId)
                            .cookie(atkCookie,rtkCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/diaries/"+userId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size", Matchers.is(members.getDiaryTotal())))
                    .andDo(MockMvcResultHandlers.print(System.out))
                    .andReturn();
        }

        @Test
        @DisplayName("[정상] 4-2.사용자 일기 조회_페이징")
        @Order(4)
        void findUserDiary_Some() throws Exception {
            Members members = memberRepository.findById(userId).get();

            // when
            MvcResult mvcResult = mockMvc.perform(get("/api/v1/diaries/"+userId)
                            .cookie(atkCookie,rtkCookie)
                            .param("page","2")
                            .param("size","3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/diaries/"+userId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print(System.out))
                    .andReturn();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(mvcResult.getResponse().getContentAsString());
            JSONArray contents = (JSONArray) parser.parse(jsonObject.get("content").toString());
            // 이후 테스트를 위한 sample diaryId init
            diaryId = ((JSONObject) parser.parse(contents.get(1).toString())).get("diaryId").toString();
        }

        @Test
        @DisplayName("[정상] 5.개별 일기 조회")
        @Order(5)
        void findDiary() throws Exception {
            Members members = memberRepository.findById(userId).get();

            // when
            MvcResult mvcResult = mockMvc.perform(get("/api/v1/diaries/"+userId+"/"+diaryId)
                            .cookie(atkCookie,rtkCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/diaries/"+userId+"/"+diaryId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId", Matchers.is(userId)))
                    .andDo(MockMvcResultHandlers.print(System.out))
                    .andReturn();
        }

        @Test
        @DisplayName("[정상] 6-1.일기 수정_최신x")
        @Order(6)
        void updateDiary() throws Exception {
            DiarySaveRequest request = getDiaryRequest(10, userId);
            request.setContent(SENTENCE);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(request);

            // when
            mockMvc.perform(put("/api/v1/diaries/"+diaryId)
                            .cookie(atkCookie,rtkCookie)
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/diaries/"+diaryId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string(SuccessResponseType.DIARY_UPDATE_SUCCESS.getMessage()))
                    .andDo(MockMvcResultHandlers.print(System.out));
            // Diary DB 확인
            Diary diary = diaryRepository.findById(diaryId).get();
            assertEquals(SENTENCE, diary.getContent());
            assertEquals(userId, diary.getUserId());
            assertEquals(LocalDate.now().atTime(9,0), diary.getModifyDate());
            assertEquals(LocalDate.of(2023,1,10).atTime(9,0), diary.getDate());
            // 사용자 DB 확인(RecentDiary 변경 안됨_최신 일기 아님)
            Members members = memberRepository.findById(userId).get();
            assertEquals(Integer.toString(20%9+1), members.getRecentDiary().getEmotion());
        }

        @Test
        @DisplayName("[정상] 6-2.일기 수정_최신")
        @Order(6)
        void updateRecentDiary() throws Exception {

            DiarySaveRequest request = getDiaryRequest(20, userId);
            request.setContent(SENTENCE);
            request.setEmotion(NEW_EMOTION);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(request);

            // when
            mockMvc.perform(put("/api/v1/diaries/"+recentDiaryId)
                            .cookie(atkCookie,rtkCookie)
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/diaries/"+recentDiaryId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string(SuccessResponseType.DIARY_UPDATE_SUCCESS.getMessage()))
                    .andDo(MockMvcResultHandlers.print(System.out));
            // Diary DB 확인
            Diary diary = diaryRepository.findById(recentDiaryId).get();
            assertEquals(NEW_EMOTION, diary.getEmotion());
            assertEquals(diary.getUserId(),userId);
            assertEquals(diary.getModifyDate(), LocalDate.now().atTime(9,0));
            assertEquals(diary.getDate(),LocalDate.of(2023,1,20).atTime(9,0));
            // 사용자 DB 확인(RecentDiary 변경 됨)
            Members members = memberRepository.findById(userId).get();
            assertEquals(NEW_EMOTION, members.getRecentDiary().getEmotion());
        }

        @Test
        @DisplayName("[정상] 7.일기 분석")
        @Order(7)
        void analyzeDiary() throws Exception {
            DiaryAnalysisRequest request = new DiaryAnalysisRequest();
            request.setSentences(Arrays.asList(SENTENCE));
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(request);

            // when
            mockMvc.perform(post("/api/v1/diaries/analyze")
                            .cookie(atkCookie,rtkCookie)
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/diaries/analyze"))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("[정상] 8-1.감정 통계_전체기간")
        @Order(8)
        void emotionStatistics_All() throws Exception {

            // when
            mockMvc.perform(get("/api/v1/diaries/"+userId+"/emotions")
                            .cookie(atkCookie,rtkCookie)
                            .servletPath("/api/v1/diaries/"+userId+"/emotions"))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("[정상] 8-2.감정 통계_기간조회")
        @Order(8)
        void emotionStatistics_withDate() throws Exception {

            // when
            mockMvc.perform(get("/api/v1/diaries/"+userId+"/emotions")
                            .cookie(atkCookie,rtkCookie)
                            .param("startDate","2023-01-05")
                            .param("endDate","2023-01-15")
                            .servletPath("/api/v1/diaries/"+userId+"/emotions"))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("[정상] 9.다이어리 삭제")
        @Order(9)
        void deleteDiary() throws Exception {

            // when
            mockMvc.perform(delete("/api/v1/diaries/"+userId+"/"+diaryId)
                            .cookie(atkCookie,rtkCookie)
                            .servletPath("/api/v1/diaries/"+userId+"/"+diaryId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andDo(MockMvcResultHandlers.print(System.out));

            // Diary DB 확인
            assertEquals(false, diaryRepository.findById(diaryId).isPresent());
            // Member DB 확인
            assertEquals(19, memberRepository.findById(userId).get().getDiaryTotal());
            // RecentDiary 변경 안됨_최신 일기 아님
            assertEquals(NEW_EMOTION, memberRepository.findById(userId).get().getRecentDiary().getEmotion());
        }

        @Test
        @DisplayName("[정상] 10.최신 다이어리 삭제")
        @Order(10)
        void deleteRecentDiary() throws Exception {

            // when
            mockMvc.perform(delete("/api/v1/diaries/"+userId+"/"+recentDiaryId)
                            .cookie(atkCookie,rtkCookie)
                            .servletPath("/api/v1/diaries/"+userId+"/"+recentDiaryId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string(SuccessResponseType.DIARY_REMOVE_SUCCESS.getMessage()))
                    .andDo(MockMvcResultHandlers.print(System.out));

            // Diary DB 확인
            assertEquals(false, diaryRepository.findById(recentDiaryId).isPresent());
            // Member DB 확인
            assertEquals(18, memberRepository.findById(userId).get().getDiaryTotal());
            // RecentDiary 변경 확인.
            assertEquals(memberRepository.findById(userId).get().getRecentDiary().getDiaryDate(), LocalDate.of(2023, 1, 19));
        }

        @Test
        @DisplayName("[정상] 11.오늘 일기 작성")
        @Order(11)
        void saveTodayDiary() throws Exception {
            // when
            Members members = Members.builder().build();
            DiarySaveRequest request = DiarySaveRequest.builder()
                    .userId(userId)
                    .content("content")
                    .date(LocalDate.now())
                    .emotion("감정없음")
                    .keywords(new String[]{"keyword"})
                    .thumbnail("thumbnail")
                    .build();

            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(request);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/diaries")
                            .cookie(atkCookie,rtkCookie)
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/diaries"))
                    //then
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string(SuccessResponseType.DIARY_SAVE_SUCCESS.getMessage()))
                    .andDo(MockMvcResultHandlers.print(System.out))
                    .andReturn();

            // Member DB 확인
            members = memberRepository.findById(userId).get();
            assertEquals(19, members.getDiaryTotal());

            assertEquals(members.getRecentDiary().getDiaryDate(), LocalDate.now());
            recentDiaryId = members.getRecentDiary().getDiaryId();

            // Cookie 설정 확인
            MockHttpServletResponse response = mvcResult.getResponse();
            assertEquals(response.getCookie("todayDiaryId").getValue(),recentDiaryId);
        }

        @Test
        @DisplayName("[정상] 12.오늘 일기 삭제")
        @Order(12)
        void deleteTodayDiary() throws Exception {

            // when
            MvcResult mvcResult = mockMvc.perform(delete("/api/v1/diaries/"+userId+"/"+recentDiaryId)
                            .cookie(atkCookie,rtkCookie)
                            .servletPath("/api/v1/diaries/"+userId+"/"+recentDiaryId))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string(SuccessResponseType.DIARY_REMOVE_SUCCESS.getMessage()))
                    .andDo(MockMvcResultHandlers.print(System.out))
                    .andReturn();

            // Diary DB 확인
            assertEquals(false, diaryRepository.findById(recentDiaryId).isPresent());
            // Member DB 확인
            assertEquals(18, memberRepository.findById(userId).get().getDiaryTotal());

            // Cookie 설정 확인
            MockHttpServletResponse response = mvcResult.getResponse();
            assertEquals("", response.getCookie("todayDiaryId").getValue());
        }

        @Test
        @DisplayName("[예외] 13-1.회원 탈퇴_잘못된 비밀번호")
        @Order(13)
        void invalidMemberRemove() throws Exception {
            MemberRemoveRequest request = new MemberRemoveRequest(userId, "wrongPassword");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(request);
            // when
            mockMvc.perform(delete("/api/v1/members/"+userId)
                            .cookie(atkCookie,rtkCookie)
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/members/"+userId))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.WRONG_PASSWORD.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.WRONG_PASSWORD.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));

            // Member DB 확인_아직 지워지면 안됨
            assertEquals('N', memberRepository.findById(userId).get().getDelYn());
        }
        @Test
        @DisplayName("[정상] 13-2.회원 탈퇴")
        @Order(14)
        void memberRemove() throws Exception {
            MemberRemoveRequest request = new MemberRemoveRequest(userId, PASSWORD);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(request);
            // when
            mockMvc.perform(delete("/api/v1/members/"+userId)
                            .cookie(atkCookie,rtkCookie)
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v1/members/"+userId))
                    //then
                    .andExpect(status().is3xxRedirection())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andDo(MockMvcResultHandlers.print(System.out));

            // Member DB 확인
            assertEquals(true, memberRepository.findById(userId).isPresent());
            assertEquals('Y', memberRepository.findById(userId).get().getDelYn());
        }
        @Test
        @DisplayName("[예외] 14.탈퇴한 사용자 계정 로그인")
        @Order(15)
        void logInDeletedUser() throws Exception {
            LoginRequest loginRequest = new LoginRequest(LOGIN_ID, PASSWORD);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(loginRequest);
            // when
            MvcResult mvcResult = mockMvc.perform(post("/api/v0/members/login")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .servletPath("/api/v0/members/login"))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.DELETED_USER.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.DELETED_USER.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out))
                    .andReturn();

            // Cookie 설정 확인
            MockHttpServletResponse response = mvcResult.getResponse();
            assertEquals(0, response.getCookies().length);


        }

    }
}
