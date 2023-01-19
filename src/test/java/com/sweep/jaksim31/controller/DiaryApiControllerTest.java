package com.sweep.jaksim31.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
        @Test
        @DisplayName("[정상]일기 저장 완료")
        public void saveDiary() throws Exception{
            String[] keywords = {"happy"};
            LocalDate date = LocalDate.of(2023, 1, 18);
            //given
            given(diaryService.saveDiary(any()))
                    .willReturn(ResponseEntity.ok(DiaryResponse.builder()
                            .userId("63c0cb6f30dc3d547e3b88bb")
                            .content("contents")
                            .diaryDate(date)
                            .emotion("happy")
                            .keywords(keywords)
                            .thumbnail("thumbnail")
                            .build()));

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/v0/diaries")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId", Matchers.is("63c0cb6f30dc3d547e3b88bb")))
                    .andExpect(jsonPath("$.content", Matchers.is("contents")))
                    .andExpect(jsonPath("$.date", Matchers.is(date.atTime(9,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                    .andExpect(jsonPath("$.modifyDate", Matchers.is(LocalDate.now().atTime(9,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                    .andExpect(jsonPath("$.emotion", Matchers.is("happy")))
                    .andExpect(jsonPath("$.keywords", Matchers.contains(keywords)))
                    .andExpect(jsonPath("$.thumbnail", Matchers.is("thumbnail")))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]사용자를 찾을 수 없을 때")
        public void failSaveDiaryNotFoundUser() throws Exception{
            String[] keywords = {"happy"};
            LocalDate date = LocalDate.of(2023, 1, 18);
            //given
            given(diaryService.saveDiary(any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/v0/diaries")
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
            String[] keywords = {"happy"};
            LocalDate date = LocalDate.of(2023, 1, 18);
            //given
            given(diaryService.saveDiary(any()))
                    .willThrow(new BizException(DiaryExceptionType.DUPLICATE_DIARY));

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/v0/diaries")
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
        @DisplayName("[예외]날짜가 유효하지 않을 때")
        public void failSaveDiaryWrongDate() throws Exception{
            String[] keywords = {"happy"};
            LocalDate date = LocalDate.of(2044, 1, 18);
            //given
            given(diaryService.saveDiary(any()))
                    .willThrow(new BizException(DiaryExceptionType.WRONG_DATE));

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("63c0cb6f30dc3d547e3b88bb", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(post("/v0/diaries")
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
    @DisplayName("일기 조회 컨트롤러")
    class findUserDiary {
        @Test
        @DisplayName("[정상]일기 조회 완료")
        public void findUserDiary() throws Exception{
            String[] keywords = {"happy"};
            LocalDate date = LocalDate.of(2023, 1, 18);
            List<DiaryInfoResponse> diaryInfoResponses = List.of(DiaryInfoResponse.builder()
                    .diaryId("diaryId")
                    .userId("userId")
                    .diaryDate(date)
                    .modifyDate(LocalDate.now())
                    .emotion("happy")
                    .keywords(keywords)
                    .thumbnail("thumbnail").build());
            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "date"));

            Page<DiaryInfoResponse> page = PageableExecutionUtils.getPage(diaryInfoResponses, pageable, ()->1);
            //given
            given(diaryService.findUserDiaries(any(),any()))
                    .willReturn(ResponseEntity.ok(page));

            //when
            mockMvc.perform(get("/v0/diaries/userId")
                            .with(csrf()) //403 에러 방지
                            .queryParam("page", String.valueOf(0))
                    )

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].userId", Matchers.is("userId")))
                    .andExpect(jsonPath("$.content[0].date", Matchers.is(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                    .andExpect(jsonPath("$.content[0].modifyDate", Matchers.is(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                    .andExpect(jsonPath("$.content[0].emotion", Matchers.is("happy")))
                    .andExpect(jsonPath("$.content[0].keywords", Matchers.contains(keywords)))
                    .andExpect(jsonPath("$.content[0].thumbnail", Matchers.is("thumbnail")))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]사용자가 없는 경우")
        public void failFindUserDiaryNotFoundUser() throws Exception{
            String[] keywords = {"happy"};
            LocalDate date = LocalDate.of(2023, 1, 18);
            List<DiaryInfoResponse> diaryInfoResponses = List.of(DiaryInfoResponse.builder()
                    .diaryId("diaryId")
                    .userId("userId")
                    .diaryDate(date)
                    .modifyDate(LocalDate.now())
                    .emotion("happy")
                    .keywords(keywords)
                    .thumbnail("thumbnail").build());
            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "date"));

            Page<DiaryInfoResponse> page = PageableExecutionUtils.getPage(diaryInfoResponses, pageable, ()->1);
            //given
            given(diaryService.findUserDiaries(any(),any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when
            mockMvc.perform(get("/v0/diaries/userId")
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
        @Test
        @DisplayName("[정상]일기 수정 완료")
        public void updateDiary() throws Exception{
            String[] keywords = {"happy"};
            LocalDate date = LocalDate.of(2023, 1, 18);

            //given
            given(diaryService.updateDiary(any(), any()))
                    .willReturn(ResponseEntity.ok(
                            DiaryResponse.of(new Diary("diaryId", DiarySaveRequest.builder().userId("userId")
                                    .content("contents")
                                    .date(date)
                                    .emotion("happy")
                                    .keywords(keywords)
                                    .thumbnail("thumbnail")
                                    .build()))));

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("userId", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/v0/diaries/diaryId")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", Matchers.is("diaryId")))
                    .andExpect(jsonPath("$.userId", Matchers.is("userId")))
                    .andExpect(jsonPath("$.content", Matchers.is("contents")))
                    .andExpect(jsonPath("$.date", Matchers.is(date.atTime(9,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                    .andExpect(jsonPath("$.modifyDate", Matchers.is(LocalDate.now().atTime(9,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                    .andExpect(jsonPath("$.emotion", Matchers.is("happy")))
                    .andExpect(jsonPath("$.keywords", Matchers.contains(keywords)))
                    .andExpect(jsonPath("$.thumbnail", Matchers.is("thumbnail")))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[예외]사용자를 찾을 수 없을 때")
        public void failUpdateDiaryNotFoundUser() throws Exception{
            String[] keywords = {"happy"};
            LocalDate date = LocalDate.of(2023, 1, 18);

            //given
            given(diaryService.updateDiary(any(), any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("userId", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/v0/diaries/diaryId")
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
            String[] keywords = {"happy"};
            LocalDate date = LocalDate.of(2023, 1, 18);

            //given
            given(diaryService.updateDiary(any(), any()))
                    .willThrow(new BizException(DiaryExceptionType.NOT_FOUND_DIARY));;

            //when
            DiarySaveRequest diarySaveRequest = new DiarySaveRequest("userId", "contents", date, "happy", keywords,"thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diarySaveRequest);

            mockMvc.perform(put("/v0/diaries/diaryId")
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
    }
    @Nested
    @DisplayName("일기 삭제 컨트롤러")
    class removeDiary {
        @Test
        @DisplayName("[정상]일기 삭제 완료")
        public void removeDiary() throws Exception{
            //given
            given(diaryService.remove(any()))
                    .willReturn(ResponseEntity.ok("diaryId"));

            //when
            mockMvc.perform(delete("/v0/diaries/diaryId")
                            .with(csrf()) //403 에러 방지
                    )

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string("diaryId"))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[정상]일기 정보가 없을 때")
        public void removeDiaryNotFoundDiary() throws Exception{
            //given
            given(diaryService.remove(any()))
                    .willThrow(new BizException(DiaryExceptionType.DELETE_NOT_FOUND_DIARY));

            //when
            mockMvc.perform(delete("/v0/diaries/diaryId")
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
        @Test
        @DisplayName("[정상]일기 분석 완료")
        public void analyzeDiary() throws Exception{
            List<String> engKeyword = List.of(new String[]{"happy", "good", "wow"});
            List<String> korKeyword = List.of(new String[]{"기쁨", "좋음", "놀라움"});
            List<String> sentences = List.of(new String[]{"문장1", "문장2", "문장3"});
            //given
            given(diaryService.analyzeDiary(any()))
                    .willReturn(ResponseEntity.ok(DiaryAnalysisResponse.builder()
                            .englishEmotion("good")
                            .koreanEmotion("좋음")
                            .englishKeywords(engKeyword)
                            .koreanKeywords(korKeyword)
                            .build()));

            //when
            DiaryAnalysisRequest diaryAnalysisRequest = new DiaryAnalysisRequest();
            diaryAnalysisRequest.setSentences(sentences);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryAnalysisRequest);

            mockMvc.perform(post("/v0/diaries/analyze")
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
        @DisplayName("[예외]감정 분석 API 오류")
        public void failAnalyzeDiaryEmotionAnalysis() throws Exception{
            List<String> engKeyword = List.of(new String[]{"happy", "good", "wow"});
            List<String> korKeyword = List.of(new String[]{"기쁨", "좋음", "놀라움"});
            List<String> sentences = List.of(new String[]{"문장1", "문장2", "문장3"});
            //given
            given(diaryService.analyzeDiary(any()))
                    .willThrow(new BizException(ThirdPartyExceptionType.NOT_ANALYZE_EMOTION));

            //when
            DiaryAnalysisRequest diaryAnalysisRequest = new DiaryAnalysisRequest();
            diaryAnalysisRequest.setSentences(sentences);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryAnalysisRequest);

            mockMvc.perform(post("/v0/diaries/analyze")
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
            List<String> engKeyword = List.of(new String[]{"happy", "good", "wow"});
            List<String> korKeyword = List.of(new String[]{"기쁨", "좋음", "놀라움"});
            List<String> sentences = List.of(new String[]{"문장1", "문장2", "문장3"});
            //given
            given(diaryService.analyzeDiary(any()))
                    .willThrow(new BizException(ThirdPartyExceptionType.NOT_EXTRACT_KEYWORD));

            //when
            DiaryAnalysisRequest diaryAnalysisRequest = new DiaryAnalysisRequest();
            diaryAnalysisRequest.setSentences(sentences);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryAnalysisRequest);

            mockMvc.perform(post("/v0/diaries/analyze")
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
            List<String> engKeyword = List.of(new String[]{"happy", "good", "wow"});
            List<String> korKeyword = List.of(new String[]{"기쁨", "좋음", "놀라움"});
            List<String> sentences = List.of(new String[]{"문장1", "문장2", "문장3"});
            //given
            given(diaryService.analyzeDiary(any()))
                    .willThrow(new BizException(ThirdPartyExceptionType.NOT_TRANSLATE_KEYWORD));

            //when
            DiaryAnalysisRequest diaryAnalysisRequest = new DiaryAnalysisRequest();
            diaryAnalysisRequest.setSentences(sentences);
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryAnalysisRequest);

            mockMvc.perform(post("/v0/diaries/analyze")
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
                    .willReturn(ResponseEntity.ok("downloadUrl/userId/"+ DATE_FORMATTER.format(ZonedDateTime.now()) + "_r_640x0_100_0_0.png"));

            //when
            DiaryThumbnailRequest diaryThumbnailRequest = new DiaryThumbnailRequest("userId","diaryId","thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryThumbnailRequest);

            mockMvc.perform(put("/v0/diaries/thumbnail")
                            .with(csrf()) //403 에러 방지
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/plain;charset=UTF-8"))
                    .andExpect(content().string("downloadUrl/userId/"+ DATE_FORMATTER.format(ZonedDateTime.now()) + "_r_640x0_100_0_0.png"))
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
            DiaryThumbnailRequest diaryThumbnailRequest = new DiaryThumbnailRequest("userId","diaryId","thumbnail");
            String jsonRequest = JsonUtil.objectMapper.writeValueAsString(diaryThumbnailRequest);

            mockMvc.perform(put("/v0/diaries/thumbnail")
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
                    .willReturn(ResponseEntity.ok(DiaryEmotionStaticsResponse.builder()
                                    .emotionStatics(emotionStatics)
                                    .build()));

            //when
            mockMvc.perform(get("/v0/diaries/userId/emotions")
                            .with(csrf())) //403 에러 방지
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.emotionStatics[0].emotion", Matchers.is(emotionStatics.get(0).getEmotion())))
                    .andExpect(jsonPath("$.emotionStatics[0].countEmotion", Matchers.is(emotionStatics.get(0).getCountEmotion())))
                    .andDo(MockMvcResultHandlers.print(System.out));
        }
        @Test
        @DisplayName("[정상]감정 통계 완료(날짜 조건이 있을 때)")
        public void emotionStatisticsWithDate() throws Exception{
            List<DiaryEmotionStatics> emotionStatics = List.of(DiaryEmotionStatics.builder().emotion("좋음").countEmotion(1).build());
//            emotionStatics.add(DiaryEmotionStatics.builder().emotion("좋음").countEmotion(1).build());

            //given
            given(diaryService.emotionStatics(any(), any()))
                    .willReturn(ResponseEntity.ok(DiaryEmotionStaticsResponse.builder()
                            .emotionStatics(emotionStatics)
                            .build()));

            //when
            mockMvc.perform(get("/v0/diaries/userId/emotions?startDate=2023-01-01&endDate=2023-01-10")
                            .with(csrf())) //403 에러 방지
                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.emotionStatics[0].emotion", Matchers.is(emotionStatics.get(0).getEmotion())))
                    .andExpect(jsonPath("$.emotionStatics[0].countEmotion", Matchers.is(emotionStatics.get(0).getCountEmotion())))
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
            mockMvc.perform(get("/v0/diaries/userId/emotions?startDate=2023-01-01&endDate=2023-01-10")
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
