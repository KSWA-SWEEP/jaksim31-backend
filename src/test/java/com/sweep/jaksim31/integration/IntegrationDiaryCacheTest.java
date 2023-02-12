package com.sweep.jaksim31.integration;


import com.sweep.jaksim31.adapter.RestPage;
import com.sweep.jaksim31.adapter.cache.DiaryCacheAdapter;
import com.sweep.jaksim31.adapter.cache.DiaryPagingCacheAdapter;
import com.sweep.jaksim31.adapter.cache.MemberCacheAdapter;
import com.sweep.jaksim31.adapter.cache.RefreshTokenCacheAdapter;
import com.sweep.jaksim31.config.EmbeddedRedisConfig;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.dto.diary.DiaryInfoResponse;
import com.sweep.jaksim31.dto.diary.DiaryResponse;
import com.sweep.jaksim31.dto.diary.DiarySaveRequest;
import com.sweep.jaksim31.dto.member.MemberSaveRequest;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.service.impl.DiaryServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ImportAutoConfiguration(EmbeddedRedisConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationDiaryCacheTest {

    private static final String MEMBER_CACHE_PREFIX = "memberCache::";
    private static final String DIARY_CACHE_PREFIX = "diaryCache::";
    private static final String LOGIN_ID = "loginId";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String PROFILE_IMAGE = "profileImage";
    private static final String INVALID_USER_ID = "adasdadasfa44";
    private static final String DIARY_PAGE_CACHE_SUFFIX = "Page request [number: 0, size 1, sort: date: DESC]";

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private DiaryServiceImpl diaryService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DiaryCacheAdapter diaryCacheAdapter;
    @Autowired
    private MemberCacheAdapter memberCacheAdapter;
    @Autowired
    private DiaryPagingCacheAdapter diaryPagingCacheAdapter;
    @Autowired
    private RefreshTokenCacheAdapter refreshTokenCacheAdapter;
    private static String userId;
    private static Members members;
    private static String diaryId;
    private static final MockHttpServletRequest request = new MockHttpServletRequest();
    private static final MockHttpServletResponse response = new MockHttpServletResponse();


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

    @BeforeAll
    public void setUp() {
        memberRepository.deleteAll();
        diaryRepository.deleteAll();
        // 테스트용으로 member entity 생성 후 db에 저장
        members = new MemberSaveRequest(LOGIN_ID, PASSWORD, USERNAME, PROFILE_IMAGE)
                .toMember(passwordEncoder, false);

        memberRepository.save(members);

        // db에 저장된 데이터로 테스트용 데이터로 생성
        userId = Objects.requireNonNull(memberRepository.findByLoginId(LOGIN_ID).orElse(null)).getId();

        // 테스트용 일기 데이터 DB에 추가
        diaryRepository.save(getDiaryRequest(1, userId).toEntity());

        // 테스트용 diaryId
        diaryId = diaryRepository.findAll().get(0).getId();

    }

    @Nested
    @DisplayName("통합 테스트 01. 개별 다이어리 캐싱 테스트")
    @TestMethodOrder(value = MethodOrderer.DisplayName.class) // 메소드 순서 지정
    class diaryCachingTest {

        @Test
        @DisplayName("1-1. [정상] 다이어리 조회 시 캐싱 데이터 생성 ")
        void successFindDiary() {
            //given
            diaryRepository.save(getDiaryRequest(1, userId).toEntity());
            // 테스트용 diaryId
            diaryId = diaryRepository.findAll().get(0).getId();

            // when
            diaryService.findDiary(userId, diaryId);
            DiaryResponse cacheResult = diaryCacheAdapter.get(DIARY_CACHE_PREFIX + diaryId);

            //then
            assertNotNull(cacheResult);
        }

        @Test
        @DisplayName("1-2. [예외] 다이어리 조회 시 예외 발생하면 캐싱 X")
        void failFindDiary() {
            //given
            diaryCacheAdapter.delete(DIARY_CACHE_PREFIX + diaryId);
            // when
            try {
                diaryService.findDiary(INVALID_USER_ID, diaryId);
            } catch (BizException ex) {
                DiaryResponse cacheResult = diaryCacheAdapter.get(DIARY_CACHE_PREFIX + diaryId);
                //then
                assertNull(cacheResult);
            }
        }
    }

    @Nested
    @DisplayName("통합 테스트 02. 다이어리 Page 캐싱 테스트")
    @TestMethodOrder(value = MethodOrderer.DisplayName.class) // 메소드 순서 지정
    class diaryPageCachingTest {

        @Test
        @DisplayName("2-1. [정상] 다이어리 페이지 조회 시 캐싱 데이터 생성 ")
        void successFindUserDiaries() {

            // when
            diaryService.findUserDiaries(userId, new HashMap<>());
            RestPage<DiaryInfoResponse> cacheResult = diaryPagingCacheAdapter.get(userId + DIARY_PAGE_CACHE_SUFFIX);

            //then
            assertNotNull(cacheResult);
        }

        @Test
        @DisplayName("2-2. [예외] 다이어리 페이지 조회 시 예외 발생하면 캐싱 X")
        void failFindUserDiaries() {

            // when
            try {
                diaryService.findUserDiaries(INVALID_USER_ID, new HashMap<>());
            } catch (BizException ex) {
                RestPage<DiaryInfoResponse> cacheResult = diaryPagingCacheAdapter.get(INVALID_USER_ID + DIARY_PAGE_CACHE_SUFFIX);
                //then
                assertNull(cacheResult);
            }
        }
    }

    @Nested
    @DisplayName("통합 테스트 03. 캐싱 삭제 테스트")
    @TestMethodOrder(value = MethodOrderer.DisplayName.class) // 메소드 순서 지정
    class diaryRemoveCachingTest {

        @Test
        @DisplayName("3-1. [정상] 다이어리 삭제 시 캐시 데이터 제거 ")
        void successRemoveDiary() {
            // 현재 테스트에서는 캐시가 정상적으로 제거 되는지만 확인하면 되므로,
            // value가 String인 refresh token cache adapter를 사용하여 간편하게 테스트 진행.

            //given
            refreshTokenCacheAdapter.put(DIARY_CACHE_PREFIX + diaryId, "test", Duration.ofSeconds(1));
            refreshTokenCacheAdapter.put(userId + DIARY_PAGE_CACHE_SUFFIX, " test", Duration.ofSeconds(1));
            refreshTokenCacheAdapter.put(MEMBER_CACHE_PREFIX + userId, "test", Duration.ofSeconds(1));

            // when
            diaryService.remove(response, userId, diaryId);

            //then
            assertNull(refreshTokenCacheAdapter.get(DIARY_CACHE_PREFIX + diaryId));
            assertNull(refreshTokenCacheAdapter.get(userId + DIARY_PAGE_CACHE_SUFFIX));
            assertNull(refreshTokenCacheAdapter.get(MEMBER_CACHE_PREFIX + userId));
        }

        @Test
        @DisplayName("3-2. [정상] 다이어리 수정 시 캐시 데이터 제거 ")
        void successUpdateDiary() {
            //given
            diaryRepository.save(getDiaryRequest(3, userId).toEntity());
            // 테스트용 diaryId
            diaryId = diaryRepository.findAll().get(0).getId();
            // 캐시데이터 저장
            refreshTokenCacheAdapter.put(DIARY_CACHE_PREFIX + diaryId, "test", Duration.ofSeconds(1));
            refreshTokenCacheAdapter.put(userId + DIARY_PAGE_CACHE_SUFFIX, " test", Duration.ofSeconds(1));
            refreshTokenCacheAdapter.put(MEMBER_CACHE_PREFIX + userId, "test", Duration.ofSeconds(1));

            // when
            diaryService.updateDiary(diaryId, getDiaryRequest(2, userId));

            //then
            assertNull(refreshTokenCacheAdapter.get(DIARY_CACHE_PREFIX + diaryId));
            assertNull(refreshTokenCacheAdapter.get(userId + DIARY_PAGE_CACHE_SUFFIX));
            assertNull(refreshTokenCacheAdapter.get(MEMBER_CACHE_PREFIX + userId));
        }

        @Test
        @DisplayName("3-3. [정상] 다이어리 등록 시 캐시 데이터 제거 ")
        void successSaveDiary() {
            //given
            diaryRepository.save(getDiaryRequest(3, userId).toEntity());
            // 테스트용 diaryId
            diaryId = diaryRepository.findAll().get(0).getId();
            // 캐시데이터 저장
            refreshTokenCacheAdapter.put(userId + DIARY_PAGE_CACHE_SUFFIX, " test", Duration.ofSeconds(1));
            refreshTokenCacheAdapter.put(MEMBER_CACHE_PREFIX + userId, "test", Duration.ofSeconds(1));

            // when
            diaryService.saveDiary(response, getDiaryRequest(5, userId));

            //then
            assertNull(refreshTokenCacheAdapter.get(userId + DIARY_PAGE_CACHE_SUFFIX));
            assertNull(refreshTokenCacheAdapter.get(MEMBER_CACHE_PREFIX + userId));
        }
    }
}
