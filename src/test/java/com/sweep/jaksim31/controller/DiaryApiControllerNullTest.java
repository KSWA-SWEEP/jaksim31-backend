package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.controller.feign.*;
import com.sweep.jaksim31.domain.auth.AuthorityRepository;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.token.RefreshTokenRepository;
import com.sweep.jaksim31.dto.diary.*;
import com.sweep.jaksim31.enums.DiaryExceptionType;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * packageName :  com.sweep.jaksim31.controller
 * fileName : DiaryApiControllerNullTest
 * author :  김주현
 * date : 2023-01-27
 * description : DiaryApiController Validator test(NULL exception 확인)
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-27              김주현             최초 생성
 */

@WebMvcTest(controllers = DiaryApiController.class)
@ExtendWith(MockitoExtension.class)
@WithMockUser // 401 에러 방지
public class DiaryApiControllerNullTest  {
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
        @DisplayName("[예외]사용자 ID가 입력되지 않았을 때")
        void failSaveDiaryUserIdIsNULL() throws Exception{
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest(null, "contents", date, "happy", keywords,"thumbnail");
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
        void failSaveDiaryContentIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", null, date, "happy", keywords,"thumbnail");
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
        void failSaveDiaryEmotionIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", date, null, keywords,"thumbnail");
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
        @DisplayName("[예외]키워드가 입력되지 않았을 때(NULL)")
        void failSaveDiaryKeywordIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", date, "happy", null,"thumbnail");
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
        void failSaveDiaryThumbnailIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", date, "happy", keywords,null);
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
        @DisplayName("[예외]일기 날짜가 입력되지 않았을 때")
        void failSaveDiaryDateIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", null, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/api/v1/diaries")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.DIARY_DATE_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.DIARY_DATE_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }

    @Nested
    @DisplayName("일기 수정 컨트롤러")
    class updateDiary {
        String[] keywords = {"happy"};
        LocalDate date = LocalDate.of(2023, 1, 18);
        @Test
        @DisplayName("[예외]사용자 ID가 입력되지 않았을 때")
        void failUpdateDiaryUserIdIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest(null, "contents", date, "happy", keywords,"thumbnail");
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
        void failUpdateDiaryContentIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", null, date, "happy", keywords,"thumbnail");
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
        void failUpdateDiaryEmotionIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", date, null, keywords,"thumbnail");
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
        @DisplayName("[예외]키워드가 입력되지 않았을 때(NULL)")
        void failUpdateDiaryKeywordIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", date, "happy", null,"thumbnail");
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
        void failUpdateDiaryThumbnailIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", date, "happy", keywords,null);
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
        @DisplayName("[예외]일기 날짜가 입력되지 않았을 때")
        void failUpdateDiaryDateIsNULL() throws Exception{
            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", null, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/api/v1/diaries/diaryId")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", Matchers.is(DiaryExceptionType.DIARY_DATE_IS_NULL.getErrorCode())))
                    .andExpect(jsonPath("$.errorMessage", Matchers.is(DiaryExceptionType.DIARY_DATE_IS_NULL.getMessage())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
    }

    @Nested
    @DisplayName("일기 분석 컨트롤러")
    class analyzeDiary {
        @Test
        @DisplayName("[예외]분석 할 문장들이 입력되지 않았을 때(NULL)")
        void failAnalyzeDiaryInputSentencesIsNull() throws Exception{
            //when
            DiaryAnalysisRequest diaryAnalysisRequest = new DiaryAnalysisRequest();
            diaryAnalysisRequest.setSentences(null);
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
    }
}
