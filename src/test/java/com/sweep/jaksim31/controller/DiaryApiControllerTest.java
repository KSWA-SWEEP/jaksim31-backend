package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.adapter.RestPage;
import com.sweep.jaksim31.controller.feign.*;
import com.sweep.jaksim31.domain.auth.AuthorityRepository;
import com.sweep.jaksim31.domain.diary.Diary;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.token.RefreshTokenRepository;
import com.sweep.jaksim31.dto.diary.*;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.DiaryExceptionType;
import com.sweep.jaksim31.exception.type.MemberExceptionType;
import com.sweep.jaksim31.exception.type.ThirdPartyExceptionType;
import com.sweep.jaksim31.service.impl.DiaryServiceImpl;
import com.sweep.jaksim31.utils.JsonUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * packageName :  com.sweep.jaksim31.controller
 * fileName : DiaryApiControllerTest
 * author :  김주현
 * date : 2023-01-17
 * description : Diary Controller 테스트
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-17           김주현             최초 생성
 * 2023-01-19           김주현             Return 타입 변경(Diary -> DiaryResponse)
 * 2023-01-20           김주현             개별 일기 조회 controller test 추가 및 DiaryResponse의 date -> diaryDate
 *                      김주현             일기 삭제 service 수정으로 인한 test 코드 수정
 * 2023-01-27           김주현             validator 테스트 코드 추가
 *                                       중복 코드 제거
 * 2023-02-01           김주현             PathValue validation 추가로 인한 test 수정
 */

@WebMvcTest(controllers = DiaryApiController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser // 401 에러 방지
public class DiaryApiControllerTest  {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DiaryServiceImpl diaryService;
    @MockBean
    private DiaryRepository diaryRepository;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private UploadImageFeign uploadImageFeign;
    @MockBean
    private DownloadImageFeign downloadImageFeign;
    @MockBean
    private KakaoApiTokenRefreshFeign apiTokenRefreshFeign;
    @MockBean
    private ExtractKeywordFeign extractKeywordFeign;
    @MockBean
    private TranslationFeign translationFeign;
    @MockBean
    private EmotionAnalysisFeign emotionAnalysisFeign;
    @MockBean
    private AuthorityRepository authorityRepository;
    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @Nested
    @DisplayName("일기 등록 컨트롤러")
    class enrollDiary {
        String[] keywords = {"happy"};
        LocalDate date = LocalDate.of(2023, 1, 18);
        @Test
        @DisplayName("[정상]일기 저장 완료")
        public void saveDiary() throws Exception{
            //given
            given(diaryService.saveDiary(any()))
                    .willReturn(DiaryResponse.builder()
                            .userId("testobjectidtestobject12")
                            .content("contents")
                            .diaryDate(date)
                            .modifyDate(LocalDate.now())
                            .emotion("happy")
                            .keywords(keywords)
                            .thumbnail("thumbnail")
                            .build());

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/api/v1/diaries")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId", Matchers.is("testobjectidtestobject12")))
                    .andExpect(jsonPath("$.content", Matchers.is("contents")))
                    .andExpect(jsonPath("$.diaryDate", Matchers.is(date.toString())))
                    .andExpect(jsonPath("$.modifyDate", Matchers.is(LocalDate.now().toString())))
                    .andExpect(jsonPath("$.emotion", Matchers.is("happy")))
                    .andExpect(jsonPath("$.keywords", Matchers.contains(keywords)))
                    .andExpect(jsonPath("$.thumbnail", Matchers.is("thumbnail")))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]사용자를 찾을 수 없을 때")
        public void failSaveDiaryNotFoundUser() throws Exception{
            //given
            given(diaryService.saveDiary(any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/api/v1/diaries")
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
        @DisplayName("[예외]이미 등록 된 일기가 있을 때")
        public void failSaveDiaryDuplicateDiary() throws Exception{
            //given
            given(diaryService.saveDiary(any()))
                    .willThrow(new BizException(DiaryExceptionType.DUPLICATE_DIARY));

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/api/v1/diaries")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.DUPLICATE_DIARY.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.DUPLICATE_DIARY.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]사용자 ID가 입력되지 않았을 때")
        public void failSaveDiaryUserIdIsNULL() throws Exception{
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("", "contents", date, "happy", keywords,"thumbnail");
            //when
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/api/v1/diaries")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.USER_ID_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.USER_ID_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]일기 내용이 입력되지 않았을 때")
        public void failSaveDiaryContentIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/api/v1/diaries")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.CONTENT_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.CONTENT_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]감정 분석 결과가 입력되지 않았을 때")
        public void failSaveDiaryEmotionIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/api/v1/diaries")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.EMOTION_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.EMOTION_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]키워드가 입력되지 않았을 때(Empty)")
        public void failSaveDiaryKeywordIsEmpty() throws Exception{
            String[] empty_keywords = new String[0];
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", empty_keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/api/v1/diaries")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.KEYWORDS_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.KEYWORDS_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]썸네일 주소가 입력되지 않았을 때")
        public void failSaveDiaryThumbnailIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", keywords,"");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/api/v1/diaries")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.THUMBNAIL_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.THUMBNAIL_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]날짜가 유효하지 않을 때")
        public void failSaveDiaryWrongDate() throws Exception{
            date = LocalDate.now().plusDays(1);
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/api/v1/diaries")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.WRONG_DATE.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.WRONG_DATE.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }
    @Nested
    @DisplayName("개별 일기 조회 컨트롤러")
    class findDiary {
        String[] keywords = {"happy"};
        LocalDate date = LocalDate.of(2023, 1, 18);
        @Test
        @DisplayName("[정상]일기 조회 완료")
        public void findDiary() throws Exception{
            DiaryResponse diary = DiaryResponse.builder()
                    .diaryId("diaryId")
                    .userId("testobjectidtestobject12")
                    .diaryDate(date)
                    .modifyDate(LocalDate.now())
                    .emotion("happy")
                    .keywords(keywords)
                    .thumbnail("thumbnail").build();

            //given
            given(diaryService.findDiary(any(),any()))
                    .willReturn(diary);

            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectidtestobject12/testobjectidtestobject12")
                            .with(csrf()) //403 에러 방지
                    )

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId", Matchers.is("testobjectidtestobject12")))
                    .andExpect(jsonPath("$.diaryDate", Matchers.is(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                    .andExpect(jsonPath("$.modifyDate", Matchers.is(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                    .andExpect(jsonPath("$.emotion", Matchers.is("happy")))
                    .andExpect(jsonPath("$.keywords", Matchers.contains(keywords)))
                    .andExpect(jsonPath("$.thumbnail", Matchers.is("thumbnail")))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @DisplayName("[예외]사용자 ID가 유효한 값이 아닌 경우")
        @Test
        void failFindDiaryInvaliUserId() throws Exception {
            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectid1234/testobjectidtestobject12")
                            .with(csrf()) //403 에러 방지
                    )

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.INVALID_ID.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.INVALID_ID.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @DisplayName("[예외]일기 ID가 유효한 값이 아닌 경우")
        @Test
        void failFindDiaryInvaliDiaryId() throws Exception {
            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectidtestobject12/testobjectid1234")
                            .with(csrf()) //403 에러 방지
                    )

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.INVALID_ID.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.INVALID_ID.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]일기가 없는 경우")
        public void failFindDiaryNotFoundDiary() throws Exception{
            //given
            given(diaryService.findDiary(any(),any()))
                    .willThrow(new BizException(DiaryExceptionType.NOT_FOUND_DIARY));

            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectidtestobject12/testobjectidtestobject12")
                            .with(csrf()) //403 에러 방지
                    )

                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.NOT_FOUND_DIARY.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.NOT_FOUND_DIARY.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]사용자의 일기가 아닌 경우")
        public void failFindDiaryNoPermission() throws Exception{
            //given
            given(diaryService.findDiary(any(),any()))
                    .willThrow(new BizException(DiaryExceptionType.NO_PERMISSION));

            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectidtestobject12/testobjectidtestobject12")
                            .with(csrf()) //403 에러 방지
                    )

                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.NO_PERMISSION.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.NO_PERMISSION.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }
    @Nested
    @DisplayName("사용자 일기 조회 컨트롤러")
    class findUserDiaries {
        String[] keywords = {"happy"};
        LocalDate date = LocalDate.of(2023, 1, 18);
        @Test
        @DisplayName("[정상]일기 조회 완료")
        public void findUserDiaries() throws Exception{
            List<DiaryInfoResponse> diaryInfoResponses = List.of(DiaryInfoResponse.builder()
                    .diaryId("diaryId")
                    .userId("testobjectidtestobject12")
                    .diaryDate(date)
                    .modifyDate(LocalDate.now())
                    .emotion("happy")
                    .keywords(keywords)
                    .thumbnail("thumbnail").build());
            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "date"));

            Page<DiaryInfoResponse> page = PageableExecutionUtils.getPage(diaryInfoResponses, pageable, ()->1);
            //given
            given(diaryService.findUserDiaries(any(),any()))
                    .willReturn(new RestPage<>(page));

            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectidtestobject12")
                            .with(csrf()) //403 에러 방지
                            .queryParam("page", String.valueOf(0))
                    )

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].userId", Matchers.is("testobjectidtestobject12")))
                    .andExpect(jsonPath("$.content[0].diaryDate", Matchers.is(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                    .andExpect(jsonPath("$.content[0].modifyDate", Matchers.is(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                    .andExpect(jsonPath("$.content[0].emotion", Matchers.is("happy")))
                    .andExpect(jsonPath("$.content[0].keywords", Matchers.contains(keywords)))
                    .andExpect(jsonPath("$.content[0].thumbnail", Matchers.is("thumbnail")))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        // TODO 사용자 일기 조건 조회 test 코드 추가
        @Test
        @DisplayName("[예외]사용자가 없는 경우")
        public void failFindUserDiariesNotFoundUser() throws Exception{
            //given
            given(diaryService.findUserDiaries(any(),any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectidtestobject12")
                            .with(csrf()) //403 에러 방지
                            .queryParam("page", String.valueOf(0))
                    )

                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }

    @Nested
    @DisplayName("일기 수정 컨트롤러")
    class updateDiary {
        String[] keywords = {"happy"};
        LocalDate date = LocalDate.of(2023, 1, 18);
        @Test
        @DisplayName("[정상]일기 수정 완료")
        public void updateDiary() throws Exception{
            //given
            given(diaryService.updateDiary(any(), any()))
                    .willReturn(
                            DiaryResponse.of(new Diary("testobjectidtestobject12", DiarySaveRequest.builder().userId("testobjectidtestobject12")
                                    .content("contents")
                                    .date(date)
                                    .emotion("happy")
                                    .keywords(keywords)
                                    .thumbnail("thumbnail")
                                    .build())));

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/api/v1/diaries/testobjectidtestobject12")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.diaryId", Matchers.is("testobjectidtestobject12")))
                    .andExpect(jsonPath("$.userId", Matchers.is("testobjectidtestobject12")))
                    .andExpect(jsonPath("$.content", Matchers.is("contents")))
                    .andExpect(jsonPath("$.diaryDate", Matchers.is(date.toString())))
                    .andExpect(jsonPath("$.modifyDate", Matchers.is(LocalDate.now().toString())))
                    .andExpect(jsonPath("$.emotion", Matchers.is("happy")))
                    .andExpect(jsonPath("$.keywords", Matchers.contains(keywords)))
                    .andExpect(jsonPath("$.thumbnail", Matchers.is("thumbnail")))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @DisplayName("[예외]일기 ID가 유효한 값이 아닌 경우")
        @Test
        void failUpdateDiaryInvaliDiaryId() throws Exception {
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectid1234", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            //when
            mockMvc.perform(put("/api/v1/diaries/testobjectid1234")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf())) //403 에러 방지

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.INVALID_ID.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.INVALID_ID.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("[예외]사용자를 찾을 수 없을 때")
        public void failUpdateDiaryNotFoundUser() throws Exception{
            //given
            given(diaryService.updateDiary(any(), any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/api/v1/diaries/testobjectidtestobject12")
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
        @DisplayName("[예외]등록 된 일기가 없을 때")
        public void failUpdateDiaryNotFoundDiary() throws Exception{
            //given
            given(diaryService.updateDiary(any(), any()))
                    .willThrow(new BizException(DiaryExceptionType.NOT_FOUND_DIARY));;

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/api/v1/diaries/testobjectidtestobject12")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.NOT_FOUND_DIARY.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.NOT_FOUND_DIARY.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]사용자 ID가 입력되지 않았을 때")
        public void failUpdateDiaryUserIdIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/api/v1/diaries/diaryId")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.USER_ID_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.USER_ID_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]일기 내용이 입력되지 않았을 때")
        public void failUpdateDiaryContentIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/api/v1/diaries/diaryId")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.CONTENT_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.CONTENT_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]감정 분석 결과가 입력되지 않았을 때")
        public void failUpdateDiaryEmotionIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/api/v1/diaries/diaryId")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.EMOTION_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.EMOTION_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]키워드가 입력되지 않았을 때(Empty)")
        public void failUpdateDiaryKeywordIsEmpty() throws Exception{
            String[] empty_keywords = new String[0];
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", empty_keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/api/v1/diaries/diaryId")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.KEYWORDS_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.KEYWORDS_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]썸네일 주소가 입력되지 않았을 때")
        public void failUpdateDiaryThumbnailIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", keywords,"");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/api/v1/diaries/diaryId")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.THUMBNAIL_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.THUMBNAIL_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]날짜가 유효하지 않을 때")
        public void failUpdateDiaryWrongDate() throws Exception{
            date = LocalDate.now().plusDays(1);
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("testobjectidtestobject12", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/api/v1/diaries/diaryId")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.WRONG_DATE.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.WRONG_DATE.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }
    @Nested
    @DisplayName("일기 삭제 컨트롤러")
    class removeDiary {
        @Test
        @DisplayName("[정상]일기 삭제 완료")
        public void removeDiary() throws Exception{
            //given
            given(diaryService.remove(any(), any()))
                    .willReturn("diaryId");

            //when
            mockMvc.perform(delete("/api/v1/diaries/testobjectidtestobject12/testobjectidtestobject12")
                            .with(csrf()) //403 에러 방지
                    )

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string("diaryId"))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @DisplayName("[예외]사용자 ID가 유효한 값이 아닌 경우")
        @Test
        void invalidRemoveInvaliUserId() throws Exception {
            //when
            mockMvc.perform(delete("/api/v1/diaries/testobjectid1234/testobjectidtestobject12")
                            .with(csrf())) //403 에러 방지

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.INVALID_ID.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.INVALID_ID.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }


        @DisplayName("[예외]일기 ID가 유효한 값이 아닌 경우")
        @Test
        void invalidRemoveInvaliDiaryId() throws Exception {
            //when
            mockMvc.perform(delete("/api/v1/diaries/testobjectidtestobject12/testobjectid1234")
                            .with(csrf())) //403 에러 방지

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.INVALID_ID.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.INVALID_ID.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("[정상]일기 정보가 없을 때")
        public void removeDiaryNotFoundDiary() throws Exception{
            //given
            given(diaryService.remove(any(), any()))
                    .willThrow(new BizException(DiaryExceptionType.DELETE_NOT_FOUND_DIARY));

            //when
            mockMvc.perform(delete("/api/v1/diaries/testobjectidtestobject12/testobjectidtestobject12")
                            .with(csrf()) //403 에러 방지
                    )

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.DELETE_NOT_FOUND_DIARY.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.DELETE_NOT_FOUND_DIARY.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }
    @Nested
    @DisplayName("일기 분석 컨트롤러")
    class analyzeDiary {
        List<String> engKeyword = List.of(new String[]{"happy", "good", "wow"});
        List<String> korKeyword = List.of(new String[]{"기쁨", "좋음", "놀라움"});
        List<String> sentences = List.of(new String[]{"문장1", "문장2", "문장3"});
        @Test
        @DisplayName("[정상]일기 분석 완료")
        public void analyzeDiary() throws Exception{
            //given
            given(diaryService.analyzeDiary(any()))
                    .willReturn(DiaryAnalysisResponse.builder()
                            .englishEmotion("good")
                            .koreanEmotion("좋음")
                            .englishKeywords(engKeyword)
                            .koreanKeywords(korKeyword)
                            .build());

            //when
            DiaryAnalysisRequest diaryAnalysisRequest = new DiaryAnalysisRequest();
            diaryAnalysisRequest.setSentences(sentences);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryAnalysisRequest);

            mockMvc.perform(post("/api/v1/diaries/analyze")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.koreanKeywords", Matchers.is(korKeyword)))
                    .andExpect(jsonPath("$.englishKeywords", Matchers.is(engKeyword)))
                    .andExpect(jsonPath("$.koreanEmotion", Matchers.is("좋음")))
                    .andExpect(jsonPath("$.englishEmotion", Matchers.is("good")))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]분석 할 문장들이 입력되지 않았을 때(Empty)")
        public void failAnalyzeDiaryInputSentencesIsEmpty() throws Exception{
            List<String> empty_sentences = List.of(new String[0]);
            //when
            DiaryAnalysisRequest diaryAnalysisRequest = new DiaryAnalysisRequest();
            diaryAnalysisRequest.setSentences(empty_sentences);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryAnalysisRequest);

            mockMvc.perform(post("/api/v1/diaries/analyze")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.INPUT_SENTENCES_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.INPUT_SENTENCES_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]감정 분석 API 오류")
        public void failAnalyzeDiaryEmotionAnalysis() throws Exception{
            //given
            given(diaryService.analyzeDiary(any()))
                    .willThrow(new BizException(ThirdPartyExceptionType.NOT_ANALYZE_EMOTION));

            //when
            DiaryAnalysisRequest diaryAnalysisRequest = new DiaryAnalysisRequest();
            diaryAnalysisRequest.setSentences(sentences);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryAnalysisRequest);

            mockMvc.perform(post("/api/v1/diaries/analyze")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(ThirdPartyExceptionType.NOT_ANALYZE_EMOTION.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(ThirdPartyExceptionType.NOT_ANALYZE_EMOTION.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]키워드 추출 API 오류")
        public void failAnalyzeDiaryKeywordExtract() throws Exception{
            //given
            given(diaryService.analyzeDiary(any()))
                    .willThrow(new BizException(ThirdPartyExceptionType.NOT_EXTRACT_KEYWORD));

            //when
            DiaryAnalysisRequest diaryAnalysisRequest = new DiaryAnalysisRequest();
            diaryAnalysisRequest.setSentences(sentences);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryAnalysisRequest);

            mockMvc.perform(post("/api/v1/diaries/analyze")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(ThirdPartyExceptionType.NOT_EXTRACT_KEYWORD.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(ThirdPartyExceptionType.NOT_EXTRACT_KEYWORD.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]번역 API 오류")
        public void failAnalyzeDiaryTranslation() throws Exception{
            //given
            given(diaryService.analyzeDiary(any()))
                    .willThrow(new BizException(ThirdPartyExceptionType.NOT_TRANSLATE_KEYWORD));

            //when
            DiaryAnalysisRequest diaryAnalysisRequest = new DiaryAnalysisRequest();
            diaryAnalysisRequest.setSentences(sentences);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryAnalysisRequest);

            mockMvc.perform(post("/api/v1/diaries/analyze")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(ThirdPartyExceptionType.NOT_TRANSLATE_KEYWORD.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(ThirdPartyExceptionType.NOT_TRANSLATE_KEYWORD.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }

    @Nested
    @DisplayName("썸네일 생성 컨트롤러")
    class saveThumbnail {
        @Test
        @DisplayName("[정상]썸네일 생성 완료")
        public void saveThumbnail() throws Exception{
            DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'");
            //given
            given(diaryService.saveThumbnail(any()))
                    .willReturn("downloadUrl/testobjectidtestobject12/"+ DATE_FORMATTER.format(ZonedDateTime.now()) + "_r_640x0_100_0_0.png");

            //when
            DiaryThumbnailRequest diaryThumbnailRequest = new DiaryThumbnailRequest("testobjectidtestobject12","diaryId","thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryThumbnailRequest);

            mockMvc.perform(put("/api/v1/diaries/thumbnail")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string("downloadUrl/testobjectidtestobject12/"+ DATE_FORMATTER.format(ZonedDateTime.now()) + "_r_640x0_100_0_0.png"))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]이미지 업로드 실패")
        public void failSaveThumbnail() throws Exception{
            DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'");
            //given
            given(diaryService.saveThumbnail(any()))
                    .willThrow(new BizException(ThirdPartyExceptionType.NOT_UPLOAD_IMAGE));

            //when
            DiaryThumbnailRequest diaryThumbnailRequest = new DiaryThumbnailRequest("testobjectidtestobject12","diaryId","thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryThumbnailRequest);

            mockMvc.perform(put("/api/v1/diaries/thumbnail")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(ThirdPartyExceptionType.NOT_UPLOAD_IMAGE.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(ThirdPartyExceptionType.NOT_UPLOAD_IMAGE.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]사용자 ID가 입력되지 않았을 때")
        public void failSaveThumbnailUserIdIsNULL() throws Exception{
            //when
            DiaryThumbnailRequest diaryThumbnailRequest = new DiaryThumbnailRequest("","diaryId","thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryThumbnailRequest);

            mockMvc.perform(put("/api/v1/diaries/thumbnail")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.USER_ID_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.USER_ID_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]일기 ID가 입력되지 않았을 때")
        public void failSaveThumbnailDiaryIdIsNULL() throws Exception{
            //when
            DiaryThumbnailRequest diaryThumbnailRequest = new DiaryThumbnailRequest("testobjectidtestobject12","","thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryThumbnailRequest);

            mockMvc.perform(put("/api/v1/diaries/thumbnail")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.DIARY_ID_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.DIARY_ID_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]썸네일 이미지 경로가 입력되지 않았을 때")
        public void failSaveThumbnailURIIsNULL() throws Exception{
            //when
            DiaryThumbnailRequest diaryThumbnailRequest = new DiaryThumbnailRequest("testobjectidtestobject12","testobjectidtestobject12","");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryThumbnailRequest);

            mockMvc.perform(put("/api/v1/diaries/thumbnail")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.THUMBNAIL_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.THUMBNAIL_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }

    @Nested
    @DisplayName("감정 통계 컨트롤러")
    class emotionStatistics {
        @Test
        @DisplayName("[정상]감정 통계 완료")
        public void emotionStatistics() throws Exception{
            List<DiaryEmotionStatics> emotionStatics = List.of(DiaryEmotionStatics.builder().emotion("좋음").countEmotion(1).build());
            //given
            given(diaryService.emotionStatics(any(), any()))
                    .willReturn(DiaryEmotionStaticsResponse.builder()
                                    .emotionStatics(emotionStatics)
                                    .startDate(LocalDate.of(1990, 1, 1))
                                    .endDate(LocalDate.now())
                                    .build());

            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectidtestobject12/emotions")
                            .with(csrf())) //403 에러 방지
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.emotionStatics[0].emotion", Matchers.is(emotionStatics.get(0).getEmotion())))
                    .andExpect(jsonPath("$.emotionStatics[0].countEmotion", Matchers.is(emotionStatics.get(0).getCountEmotion())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @DisplayName("[예외]일기 ID가 유효한 값이 아닌 경우")
        @Test
        void failEmotionStatisticsInvalidId() throws Exception {
            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectid1234/emotions")
                            .with(csrf())) //403 에러 방지

                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.INVALID_ID.getMessage())))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.INVALID_ID.getErrorCode())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }

        @Test
        @DisplayName("[정상]감정 통계 완료(날짜 조건이 있을 때)")
        public void emotionStatisticsWithDate() throws Exception{
            List<DiaryEmotionStatics> emotionStatics = List.of(DiaryEmotionStatics.builder().emotion("좋음").countEmotion(1).build());
//            emotionStatics.add(DiaryEmotionStatics.builder().emotion("좋음").countEmotion(1).build());
            Map<String, Object> param = new HashMap<>();
            param.put("startDate", "2023-01-01");
            param.put("endDate","2023-01-20");

            //given
            given(diaryService.emotionStatics(any(), any()))
                    .willReturn(DiaryEmotionStaticsResponse.builder()
                            .emotionStatics(emotionStatics)
                            .startDate(LocalDate.parse(param.get("startDate").toString()))
                            .endDate(LocalDate.parse(param.get("endDate").toString()))
                            .build());

            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectidtestobject12/emotions?startDate=2023-01-01&endDate=2023-01-20")
                            .with(csrf())) //403 에러 방지
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.emotionStatics[0].emotion", Matchers.is(emotionStatics.get(0).getEmotion())))
                    .andExpect(jsonPath("$.emotionStatics[0].countEmotion", Matchers.is(emotionStatics.get(0).getCountEmotion())))
                    .andExpect(jsonPath("$.startDate",Matchers.is(param.get("startDate"))))
                    .andExpect(jsonPath("$.endDate",Matchers.is(param.get("endDate"))))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]사용자를 찾을 수 없을 때")
        public void failEmotionStatisticsNotFoundUser() throws Exception{
            DiaryEmotionStatics[] diaryEmotionStatics = {new DiaryEmotionStatics("좋음", 1), new DiaryEmotionStatics("나쁨", 1)};
            List<DiaryEmotionStatics> emotionStatics = new ArrayList<>(List.of(diaryEmotionStatics));

            //given
            given(diaryService.emotionStatics(any(), any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when
            mockMvc.perform(get("/api/v1/diaries/testobjectidtestobject12/emotions?startDate=2023-01-01&endDate=2023-01-10")
                            .with(csrf())) //403 에러 방지
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(MemberExceptionType.NOT_FOUND_USER.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }
}
