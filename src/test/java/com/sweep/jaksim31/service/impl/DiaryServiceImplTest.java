package com.sweep.jaksim31.service.impl;

import com.sweep.jaksim31.controller.feign.EmotionAnalysisFeign;
import com.sweep.jaksim31.domain.diary.Diary;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.dto.diary.*;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.DiaryExceptionType;
import com.sweep.jaksim31.exception.type.MemberExceptionType;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * packageName :  com.sweep.jaksim31.service.impl
 * fileName : DiaryServiceImplTest
 * author :  김주현
 * date : 2023-01-19
 * description : Diary Service Test
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-19           김주현             최초 생성
 */
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "username", password = "password", roles = "ROLE_USER")

public class DiaryServiceImplTest {
    @InjectMocks
    private DiaryServiceImpl diaryService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private DiaryRepository diaryRepository;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private EmotionAnalysisFeign emotionAnalysisFeign;

    private static MockedStatic<DiaryResponse> diaryResponse;
    private static MockedStatic<DiaryInfoResponse> diaryInfoResponse;
    private static MockedStatic<DiaryEmotionStaticsResponse> diaryEmotionStaticsResponse;
    private static MockedStatic<PageableExecutionUtils> pageableExecutionUtils;
    private static MockedStatic<Page> pageMockedStatic;

    private static DiarySaveRequest diarySaveRequest;
    private static DiaryAnalysisRequest diaryAnalysisRequest;
    private static DiaryThumbnailRequest diaryThumbnailRequest;

    private static String[] keywords;
    private static LocalDate diaryDate;

    @BeforeAll
    public static void setup(){
        keywords = new String[]{"happy"};
        diaryDate = LocalDate.of(2023, 1, 7);

        diaryResponse = mockStatic(DiaryResponse.class);
        diaryInfoResponse = mockStatic(DiaryInfoResponse.class);
        pageableExecutionUtils = mockStatic(PageableExecutionUtils.class);
        pageMockedStatic = mockStatic(Page.class);
        diaryEmotionStaticsResponse = mockStatic(DiaryEmotionStaticsResponse.class);

        diarySaveRequest = DiarySaveRequest.builder()
                .userId("userId")
                .content("testContent")
                .date(diaryDate)
                .emotion("emotion")
                .keywords(keywords)
                .thumbnail("thumbnail")
                .build();

//        diaryAnalysisRequest.setSentences(List.of("testSentence","testSentence2"));
//        diaryAnalysisRequest.setLang("en");
    }
    @AfterAll
    public static void finish(){
        diaryResponse.close();
        diaryInfoResponse.close();
        diaryEmotionStaticsResponse.close();
        pageMockedStatic.close();
        pageableExecutionUtils.close();

    }
    /** TODO
     *   일기 저장 Service 구현 완료 후 Test 짜기
     *   일기 분석 코드 리팩토링 후 Test 짜기
     */
//    @Nested
//    @DisplayName("일기 저장 서비스")
//    class saveDiary{
//        @Test
//        @DisplayName("정상인 경우")
//        void saveDiary(){
//            // given
//            Diary diary = diarySaveRequest.toEntity();
//
//
//            // when
//
//            // then
//        }
//    }
    @Nested
    @DisplayName("일기 수정 서비스")
    class updateDiary{
        String diaryId = "diaryId";
        String userId = "userId";

        @Test
        @DisplayName("[정상]일기 수정 성공_일기 존재, 회원 존재")
        void updateDiary(){
            // given
            Diary updatedDiary = new Diary(diaryId, diarySaveRequest);
            assert updatedDiary != null;
            DiaryResponse diaryResponse = new DiaryResponse(diaryId, userId, "testContext", diaryDate, LocalDate.now(), "emotion", keywords, "thumbnail");
            // 일기가 존재하고
            given(diaryRepository.findById(diaryId))
                    .willReturn(Optional.of(updatedDiary));
            // 사용자가 존재할 때
            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(Members.builder().build()));
            // 일기 저장 시 Diary 리턴
            given(diaryRepository.save(any()))
                    .willReturn(updatedDiary);
            // DiaryResponse 응답.
            given(DiaryResponse.of(updatedDiary))
                    .willReturn(diaryResponse);

            // when
            ResponseEntity<DiaryResponse> result = diaryService.updateDiary(diaryId, diarySaveRequest);

            // then
            DiaryResponse expected = result.getBody();
            assert expected != null;
            assertEquals(expected.getUserId(), diarySaveRequest.getUserId());
            assertEquals(expected.getDiaryDate(), diarySaveRequest.getDate());
            assertEquals(expected.getModifyDate(), LocalDate.now());

            verify(diaryRepository, times(1)).findById(diaryId);
            verify(memberRepository, times(1)).findById(userId);
            verify(diaryRepository, times(1)).save(any());
        }
        @Test
        @DisplayName("[예외]일기가 존재하지 않을 때, 저장 X")
        void failUpdateDiaryNotFoundDiary(){
            // given
            Diary updatedDiary = new Diary(diaryId, diarySaveRequest);
            assert updatedDiary != null;
            given(diaryRepository.findById(any()))
                    .willThrow(new BizException(DiaryExceptionType.NOT_FOUND_DIARY));

            // when
            // then
            assertThrows(BizException.class, () -> diaryService.updateDiary(diaryId, diarySaveRequest));
            verify(diaryRepository, never()).save(updatedDiary);
        }
        @Test
        @DisplayName("[예외]사용자가 존재하지 않을 때, 저장 X")
        void failUpdateDiaryNotFoundUser(){
            // given
            Diary updatedDiary = new Diary(diaryId, diarySaveRequest);
            assert updatedDiary != null;
            given(diaryRepository.findById(any()))
                    .willReturn(Optional.of(updatedDiary));
            given(memberRepository.findById(any()))
                    .willThrow(new BizException(MemberExceptionType.NOT_FOUND_USER));

            // when
            // then
            assertThrows(BizException.class, () -> diaryService.updateDiary(diaryId, diarySaveRequest));
            verify(diaryRepository, never()).save(updatedDiary);
        }
    }

    @Nested
    @DisplayName("일기 삭제 서비스")
    class removeDiary{
        String diaryId = "diaryId";
        @Test
        @DisplayName("[정상]일기 삭제 성공")
        void removeDiary(){
            // given
            Diary diary = new Diary(diaryId, diarySaveRequest);
            // 삭제하려고 하는 일기가 존재함
            given(diaryRepository.findById(diaryId))
                    .willReturn(Optional.of(diary));

            // when
            String result = diaryService.remove(diaryId).getBody();

            // then
            assertEquals(result, diaryId);

            verify(diaryRepository, times(1)).findById(diaryId);
            verify(diaryRepository, times(1)).delete(diary);
        }
        @Test
        @DisplayName("[예외]일기가 존재하지 않을 때")
        void failUpdateDiaryNotFoundDiary(){
            // given
            given(diaryRepository.findById(diaryId))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(BizException.class, () -> diaryService.remove(diaryId));
            verify(diaryRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("개별 일기 조회 서비스")
    class findDiary{
        String diaryId = "diaryId";
        String userId = "userId";
        @Test
        @DisplayName("[정상]일기 조회 성공")
        void findDiary(){
            // given
            Diary diary = diarySaveRequest.toEntity();
            DiaryResponse diaryResponse = new DiaryResponse(diaryId,userId, "testContext", diaryDate, LocalDate.now(), "emotion", keywords, "thumbnail");

            given(diaryRepository.findById(diaryId))
                    .willReturn(Optional.of(diary));
            given(DiaryResponse.of(diary))
                    .willReturn(diaryResponse);

            // when
            ResponseEntity<DiaryResponse> result = diaryService.findDiary(userId, diaryId);

            // then
            DiaryResponse expected = result.getBody();
            assert expected != null;
            assertEquals(expected.getDiaryId(), diaryResponse.getDiaryId());
            assertEquals(expected.getDiaryDate(), diaryResponse.getDiaryDate());
            assertEquals(expected.getUserId(), diaryResponse.getUserId());
            verify(diaryRepository, times(1)).findById(diaryId);
        }
        @Test
        @DisplayName("[예외]일기가 존재하지 않을 때")
        void failFindDiaryNotFoundDiary(){
            // given
            given(diaryRepository.findById(diaryId))
//                    .willThrow(new BizException(DiaryExceptionType.NOT_FOUND_DIARY));
                    .willReturn(Optional.empty()); // 알아서 exception에 걸림

            // when
            // then
            assertThrows(BizException.class, () -> diaryService.findDiary(userId, diaryId));
            verify(diaryRepository, times(1)).findById(any());
        }
        @Test
        @DisplayName("[예외]사용자의 일기가 아닐 경우")
        void failFindDiaryNoPermission(){
            Diary diary = diarySaveRequest.toEntity();

            // given
            given(diaryRepository.findById(diaryId))
                    .willReturn(Optional.of(diary));

            // when
            // then
            assertThrows(BizException.class, () -> diaryService.findDiary("DifferentUserId", diaryId));
            verify(diaryRepository, times(1)).findById(any());
        }
    }

    @Nested
    @DisplayName("사용자 일기 조회 서비스")
    class findUserDiary{
        String userId = "userId";

        @Test
        @DisplayName("[정상]사용자 일기 조회 성공_page,size,sort")
        void findUserDiaryByPageSizeSort(){
            // given
            Diary diary = diarySaveRequest.toEntity();
            DiaryInfoResponse diaryInfoResponse = new DiaryInfoResponse("diaryId", userId, diaryDate, LocalDate.now(), "emotion", keywords, "thumbnail");
            List<DiaryInfoResponse> diaryInfoResponses = List.of(diaryInfoResponse);

            Map<String, String> param = new HashMap<>();
            param.put("page", "0");
            param.put("size","1");
            param.put("sort","asc");

            Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "date"));
            Page<Object> page = new PageImpl(diaryInfoResponses, pageable, 1);

            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(Members.builder().build()));
            given(mongoTemplate.find(any(),any(),any()))
                    .willReturn(List.of(diary));
            given(PageableExecutionUtils.getPage(any(),any(),any()))
                    .willReturn(page);

            // when
            ResponseEntity<Page<DiaryInfoResponse>> result = diaryService.findUserDiaries(userId,param);

            // then
            Page<DiaryInfoResponse> expected = result.getBody();
            assert expected != null;
            assertEquals(expected.getPageable(), pageable);
            assertEquals(expected.getSize(), page.getSize());
            assertEquals(expected.getContent(), diaryInfoResponses);

            verify(memberRepository, times(1)).findById(userId);
            verify(mongoTemplate, times(1)).find(any(),any(),any());
        }
        @Test
        @DisplayName("[정상]사용자 일기 조회 성공_page,size")
        void findUserDiaryByPageSize(){
            // given
            Diary diary = diarySaveRequest.toEntity();
            DiaryInfoResponse diaryInfoResponse = new DiaryInfoResponse("diaryId", userId, diaryDate, LocalDate.now(), "emotion", keywords, "thumbnail");
            List<DiaryInfoResponse> diaryInfoResponses = List.of(diaryInfoResponse);

            Map<String, String> param = new HashMap<>();
            param.put("page", "0");
            param.put("size","1");

            // Input으로 입력되는 값이 없을 경우, sort default value = "date", DESC
            Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "date"));
            Page<Object> page = new PageImpl(diaryInfoResponses, pageable, 1);

            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(Members.builder().build()));
            given(mongoTemplate.find(any(),any(),any()))
                    .willReturn(List.of(diary));
            given(PageableExecutionUtils.getPage(any(),any(),any()))
                    .willReturn(page);

            // when
            ResponseEntity<Page<DiaryInfoResponse>> result = diaryService.findUserDiaries(userId,param);

            // then
            Page<DiaryInfoResponse> expected = result.getBody();
            assert expected != null;
            assertEquals(expected.getPageable(), pageable);
            assertEquals(expected.getSize(), page.getSize());
            assertEquals(expected.getSort(), page.getSort());
            assertEquals(expected.getContent(), diaryInfoResponses);

            verify(memberRepository, times(1)).findById(userId);
            verify(mongoTemplate, times(1)).find(any(),any(),any());
        }
        @Test
        @DisplayName("[정상]사용자 일기 조회 성공_page")
        void findUserDiaryByPage(){
            // given
            Diary diary = diarySaveRequest.toEntity();
            DiaryInfoResponse diaryInfoResponse = new DiaryInfoResponse("diaryId", userId, diaryDate, LocalDate.now(), "emotion", keywords, "thumbnail");
            List<DiaryInfoResponse> diaryInfoResponses = List.of(diaryInfoResponse);

            Map<String, String> param = new HashMap<>();
            param.put("page", "0");
            // Input으로 입력되는 값이 없을 경우, size default value = 9, sort default value = "date", DESC
            Pageable pageable = PageRequest.of(0, 9, Sort.by(Sort.Direction.DESC, "date"));
            Page<Object> page = new PageImpl(diaryInfoResponses, pageable, 1);

            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(Members.builder().build()));
            given(mongoTemplate.find(any(),any(),any()))
                    .willReturn(List.of(diary));
            given(PageableExecutionUtils.getPage(any(),any(),any()))
                    .willReturn(page);

            // when
            ResponseEntity<Page<DiaryInfoResponse>> result = diaryService.findUserDiaries(userId,param);

            // then
            Page<DiaryInfoResponse> expected = result.getBody();
            assert expected != null;
            assertEquals(expected.getPageable(), pageable);
            assertEquals(expected.getSize(), page.getSize());
            assertEquals(expected.getSort(), page.getSort());
            assertEquals(expected.getContent(), diaryInfoResponses);

            verify(memberRepository, times(1)).findById(userId);
            verify(mongoTemplate, times(1)).find(any(),any(),any());
        }
        @Test
        @DisplayName("[정상]사용자 일기 조회 성공_no params")
        void findUserDiary(){
            // given
            Diary diary = diarySaveRequest.toEntity();
            DiaryInfoResponse diaryInfoResponse = new DiaryInfoResponse("diaryId", userId, diaryDate, LocalDate.now(), "emotion", keywords, "thumbnail");
            List<DiaryInfoResponse> diaryInfoResponses = List.of(diaryInfoResponse);

            Map<String, String> param = new HashMap<>();
            // page default value = 0, size default value = 9, sort default value = "date", DESC
            Pageable pageable = PageRequest.of(0, 9, Sort.by(Sort.Direction.DESC, "date"));
            Page<Object> page = new PageImpl(diaryInfoResponses, pageable, 1);

            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(Members.builder().build()));
            given(mongoTemplate.find(any(),any(),any()))
                    .willReturn(List.of(diary));
            given(PageableExecutionUtils.getPage(any(),any(),any()))
                    .willReturn(page);

            // when
            ResponseEntity<Page<DiaryInfoResponse>> result = diaryService.findUserDiaries(userId,param);

            // then
            Page<DiaryInfoResponse> expected = result.getBody();
            assert expected != null;
            assertEquals(expected.getPageable(), pageable);
            assertEquals(expected.getSize(), page.getSize());
            assertEquals(expected.getSort(), page.getSort());
            assertEquals(expected.getContent(), diaryInfoResponses);

            verify(memberRepository, times(1)).findById(userId);
            verify(mongoTemplate, times(1)).find(any(),any(),any());
        }
        @Test
        @DisplayName("[예외]사용자가 존재하지 않을 때")
        void failFindUserDiaryNotFoundUser(){
            // given
            Map<String, String> param = new HashMap<>();
            given(memberRepository.findById(userId))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(BizException.class, () -> diaryService.findUserDiaries(userId, param));
            verify(mongoTemplate, never()).find(any(),any(),any());
        }
    }

    @Nested
    @DisplayName("감정 통계 서비스")
    class emotionStatics{
        String userId = "userId";
        @Test
        @DisplayName("[정상]감정 통계 성공_전체기간(기간 조건이 들어오지 않았을 때)")
        void emotionStatics(){
            Diary diary = diarySaveRequest.toEntity();
            DiaryEmotionStatics diaryEmotionStatics = new DiaryEmotionStatics("emotion", 1);

            Map<String, Object> param = new HashMap<>();

            List<DiaryEmotionStatics> emotionStatics = List.of(diaryEmotionStatics);
            AggregationResults<Object> aggregation = new AggregationResults<Object>(Collections.singletonList(emotionStatics), new Document());
            DiaryEmotionStaticsResponse diaryEmotionStaticsResponse = new DiaryEmotionStaticsResponse(emotionStatics,LocalDate.of(1990, 1, 1),LocalDate.now());

            // given
            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(Members.builder().build()));
            given(mongoTemplate.aggregate(any(), (Class<?>) any(), any()))
                    .willReturn(aggregation);
            given(DiaryEmotionStaticsResponse.of(any(),any(),any()))
                    .willReturn(diaryEmotionStaticsResponse);

            // when
            ResponseEntity<DiaryEmotionStaticsResponse> result = diaryService.emotionStatics(userId,param);

            // then
            DiaryEmotionStaticsResponse expected = result.getBody();
            assert expected != null;
            assertEquals(expected.getEmotionStatics(), emotionStatics);
            assertEquals(expected.getStartDate(), diaryEmotionStaticsResponse.getStartDate());
            assertEquals(expected.getEndDate(), diaryEmotionStaticsResponse.getEndDate());
            verify(memberRepository, times(1)).findById(userId);
            verify(mongoTemplate, times(1)).aggregate(any(), (Class<?>) any(), any());
        }
        @Test
        @DisplayName("[정상]감정 통계 성공_기간 조회")
        void emotionStaticsStartDateEndDate(){
            Diary diary = diarySaveRequest.toEntity();
            DiaryEmotionStatics diaryEmotionStatics = new DiaryEmotionStatics("emotion", 1);

            Map<String, Object> param = new HashMap<>();
            param.put("startDate", "2023-01-01");
            param.put("endDate","2023-01-20");

            List<DiaryEmotionStatics> emotionStatics = List.of(diaryEmotionStatics);
            AggregationResults<Object> aggregation = new AggregationResults<Object>(Collections.singletonList(emotionStatics), new Document());
            DiaryEmotionStaticsResponse diaryEmotionStaticsResponse = new DiaryEmotionStaticsResponse(emotionStatics,LocalDate.parse("2023-01-01"),LocalDate.parse("2023-01-20"));

            // given
            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(Members.builder().build()));
            given(mongoTemplate.aggregate(any(), (Class<?>) any(), any()))
                    .willReturn(aggregation);
            given(DiaryEmotionStaticsResponse.of(any(),any(),any()))
                    .willReturn(diaryEmotionStaticsResponse);

            // when
            ResponseEntity<DiaryEmotionStaticsResponse> result = diaryService.emotionStatics(userId,param);

            // then
            DiaryEmotionStaticsResponse expected = result.getBody();
            assert expected != null;
            assertEquals(expected.getEmotionStatics(), emotionStatics);

            verify(memberRepository, times(1)).findById(userId);
            assertEquals(expected.getStartDate(), diaryEmotionStaticsResponse.getStartDate());
            assertEquals(expected.getEndDate(), diaryEmotionStaticsResponse.getEndDate());
            verify(mongoTemplate, times(1)).aggregate(any(), (Class<?>) any(), any());
        }
        @Test
        @DisplayName("[정상]감정 통계 성공_startDate만 있을 때")
        void emotionStaticsStartDateOnly(){
            Diary diary = diarySaveRequest.toEntity();
            DiaryEmotionStatics diaryEmotionStatics = new DiaryEmotionStatics("emotion", 1);

            Map<String, Object> param = new HashMap<>();
            param.put("startDate", "2023-01-01");

            List<DiaryEmotionStatics> emotionStatics = List.of(diaryEmotionStatics);
            AggregationResults<Object> aggregation = new AggregationResults<Object>(Collections.singletonList(emotionStatics), new Document());
            DiaryEmotionStaticsResponse diaryEmotionStaticsResponse = new DiaryEmotionStaticsResponse(emotionStatics,LocalDate.parse(param.get("startDate").toString()),LocalDate.now());

            // given
            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(Members.builder().build()));
            given(mongoTemplate.aggregate(any(), (Class<?>) any(), any()))
                    .willReturn(aggregation);
            given(DiaryEmotionStaticsResponse.of(any(),any(),any()))
                    .willReturn(diaryEmotionStaticsResponse);

            // when
            ResponseEntity<DiaryEmotionStaticsResponse> result = diaryService.emotionStatics(userId,param);

            // then
            DiaryEmotionStaticsResponse expected = result.getBody();
            assert expected != null;
            assertEquals(expected.getEmotionStatics(), emotionStatics);
            assertEquals(expected.getStartDate(), diaryEmotionStaticsResponse.getStartDate());
            assertEquals(expected.getEndDate(), diaryEmotionStaticsResponse.getEndDate());
            verify(memberRepository, times(1)).findById(userId);
            verify(mongoTemplate, times(1)).aggregate(any(), (Class<?>) any(), any());
        }
        @Test
        @DisplayName("[정상]감정 통계 성공_endDate만 있을 때")
        void emotionStaticsEndDateOnly(){
            Diary diary = diarySaveRequest.toEntity();
            DiaryEmotionStatics diaryEmotionStatics = new DiaryEmotionStatics("emotion", 1);

            Map<String, Object> param = new HashMap<>();
            param.put("endDate","2023-01-20");

            List<DiaryEmotionStatics> emotionStatics = List.of(diaryEmotionStatics);
            AggregationResults<Object> aggregation = new AggregationResults<Object>(Collections.singletonList(emotionStatics), new Document());
            DiaryEmotionStaticsResponse diaryEmotionStaticsResponse = new DiaryEmotionStaticsResponse(emotionStatics,LocalDate.of(1990, 1, 1),LocalDate.parse(param.get("endDate").toString()));

            // given
            given(memberRepository.findById(userId))
                    .willReturn(Optional.of(Members.builder().build()));
            given(mongoTemplate.aggregate(any(), (Class<?>) any(), any()))
                    .willReturn(aggregation);
            given(DiaryEmotionStaticsResponse.of(any(),any(),any()))
                    .willReturn(diaryEmotionStaticsResponse);

            // when
            ResponseEntity<DiaryEmotionStaticsResponse> result = diaryService.emotionStatics(userId,param);

            // then
            DiaryEmotionStaticsResponse expected = result.getBody();
            assert expected != null;
            assertEquals(expected.getEmotionStatics(), emotionStatics);
            assertEquals(expected.getStartDate(), diaryEmotionStaticsResponse.getStartDate());
            assertEquals(expected.getEndDate(), diaryEmotionStaticsResponse.getEndDate());
            verify(memberRepository, times(1)).findById(userId);
            verify(mongoTemplate, times(1)).aggregate(any(), (Class<?>) any(), any());
        }
        @Test
        @DisplayName("[예외]사용자가 존재하지 않을 때")
        void failEmotionStaticsNotFoundUser(){
            Diary diary = diarySaveRequest.toEntity();
            DiaryEmotionStatics diaryEmotionStatics = new DiaryEmotionStatics("emotion", 1);
            List<DiaryEmotionStatics> emotionStatics = List.of(diaryEmotionStatics);
            Map<String, Object> param = new HashMap<>();
            param.put("startDate", "2023-01-01");
            param.put("endDate","2023-01-20");

            // given
            given(memberRepository.findById(userId))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThrows(BizException.class, () -> diaryService.emotionStatics(userId, param));

            verify(memberRepository, times(1)).findById(userId);
            verify(mongoTemplate, never()).aggregate(any(), (Class<?>) any(), any());
        }
    }

//    @Nested
//    @DisplayName("일기 분석 서비스")
//    class analyzeDiary{
//        List<String> koreanKeywords = List.of("사과","키위","바나나");
//        List<String> englishKeywords = List.of("apple","kiwi","banana");
//        String koreanEmotion = "좋음";
//        String englishEmotion = "good";
//        @Test
//        @DisplayName("정상인 경우")
//        void analyzeDiary(){
//            // given
//            EmotionAnalysisRequest emotionAnalysisRequest = new EmotionAnalysisRequest(diaryAnalysisRequest.getSentences().get(0));
//            ResponseEntity<JSONObject> emotionAnalysisResult = emotionAnalysisFeign.emotionAnalysis(emotionAnalysisRequest);
//            given(emotionAnalysisFeign.emotionAnalysis(emotionAnalysisRequest));
//            // when
//
//            // then
//        }
//    }

}
