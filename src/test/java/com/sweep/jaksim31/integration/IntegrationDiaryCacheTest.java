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
        // ?????????????????? member entity ?????? ??? db??? ??????
        members = new MemberSaveRequest(LOGIN_ID, PASSWORD, USERNAME, PROFILE_IMAGE)
                .toMember(passwordEncoder, false);

        memberRepository.save(members);

        // db??? ????????? ???????????? ???????????? ???????????? ??????
        userId = Objects.requireNonNull(memberRepository.findByLoginId(LOGIN_ID).orElse(null)).getId();

        // ???????????? ?????? ????????? DB??? ??????
        diaryRepository.save(getDiaryRequest(1, userId).toEntity());

        // ???????????? diaryId
        diaryId = diaryRepository.findAll().get(0).getId();

    }

    @Nested
    @DisplayName("?????? ????????? 01. ?????? ???????????? ?????? ?????????")
    @TestMethodOrder(value = MethodOrderer.DisplayName.class) // ????????? ?????? ??????
    class diaryCachingTest {

        @Test
        @DisplayName("1-1. [??????] ???????????? ?????? ??? ?????? ????????? ?????? ")
        void successFindDiary() {
            //given
            diaryRepository.save(getDiaryRequest(1, userId).toEntity());
            // ???????????? diaryId
            diaryId = diaryRepository.findAll().get(0).getId();

            // when
            diaryService.findDiary(userId, diaryId);
            DiaryResponse cacheResult = diaryCacheAdapter.get(DIARY_CACHE_PREFIX + diaryId);

            //then
            assertNotNull(cacheResult);
        }

        @Test
        @DisplayName("1-2. [??????] ???????????? ?????? ??? ?????? ???????????? ?????? X")
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
    @DisplayName("?????? ????????? 02. ???????????? Page ?????? ?????????")
    @TestMethodOrder(value = MethodOrderer.DisplayName.class) // ????????? ?????? ??????
    class diaryPageCachingTest {

        @Test
        @DisplayName("2-1. [??????] ???????????? ????????? ?????? ??? ?????? ????????? ?????? ")
        void successFindUserDiaries() {

            // when
            diaryService.findUserDiaries(userId, new HashMap<>());
            RestPage<DiaryInfoResponse> cacheResult = diaryPagingCacheAdapter.get(userId + DIARY_PAGE_CACHE_SUFFIX);

            //then
            assertNotNull(cacheResult);
        }

        @Test
        @DisplayName("2-2. [??????] ???????????? ????????? ?????? ??? ?????? ???????????? ?????? X")
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
    @DisplayName("?????? ????????? 03. ?????? ?????? ?????????")
    @TestMethodOrder(value = MethodOrderer.DisplayName.class) // ????????? ?????? ??????
    class diaryRemoveCachingTest {

        @Test
        @DisplayName("3-1. [??????] ???????????? ?????? ??? ?????? ????????? ?????? ")
        void successRemoveDiary() {
            // ?????? ?????????????????? ????????? ??????????????? ?????? ???????????? ???????????? ?????????,
            // value??? String??? refresh token cache adapter??? ???????????? ???????????? ????????? ??????.

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
        @DisplayName("3-2. [??????] ???????????? ?????? ??? ?????? ????????? ?????? ")
        void successUpdateDiary() {
            //given
            diaryRepository.save(getDiaryRequest(3, userId).toEntity());
            // ???????????? diaryId
            diaryId = diaryRepository.findAll().get(0).getId();
            // ??????????????? ??????
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
        @DisplayName("3-3. [??????] ???????????? ?????? ??? ?????? ????????? ?????? ")
        void successSaveDiary() {
            //given
            diaryRepository.save(getDiaryRequest(3, userId).toEntity());
            // ???????????? diaryId
            diaryId = diaryRepository.findAll().get(0).getId();
            // ??????????????? ??????
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
