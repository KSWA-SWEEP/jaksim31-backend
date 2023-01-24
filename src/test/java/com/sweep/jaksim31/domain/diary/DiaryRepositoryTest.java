package com.sweep.jaksim31.domain.diary;

import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.dto.diary.DiaryInfoResponse;
import com.sweep.jaksim31.util.ExecutionTimeTestExecutionListener;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Sort.by;

/**
 * packageName :  com.sweep.jaksim31.domain.diary
 * fileName : DiaryRepositoryTest
 * author :  방근호
 * date : 2023-01-20
 * description : Diary Repository Test
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-20           방근호             최초 생성
 * 2023-01-21           김주현             테스트 케이스 추가 및 수정
 */

@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, ExecutionTimeTestExecutionListener.class})
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class) // 메소드 순서 지정
class DiaryRepositoryTest {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    DiaryRepository diaryRepository;

    List<Diary> diaryList = new ArrayList<>();

    public Diary getDiary(int num) {
        return Diary.builder()
                .userId("63c0cb6f30dc3d547e3b88bb")
                .content("content" + num)
                .date(LocalDate.of(2023, 1, num))
                .emotion(Integer.toString(num))
                .keywords(new String[]{"keyword" + num})
                .thumbnail("thumbnail" + num)
                .build();
    }

    @BeforeAll
    void setup() {
        //given
        for (int i = 1; i <= 20; i++)
            diaryList.add(getDiary(i));
    }

    @Test
    @Order(1)
    @DisplayName("일기 저장 및 조회 (Create)")
    void save() {
        //when
        diaryRepository.saveAll(diaryList);
        Diary diary = diaryRepository.findAll().get(0);

        //then
        assertEquals(diaryRepository.findAll().size(), 20);
        assertTrue(diary.getContent().startsWith("content"));
        assertEquals(Arrays.stream(diary.getKeywords()).count(), 1);
    }

    @Test
    @Order(2)
    @DisplayName("일기 삭제 (Delete) - 63c0cb6f30dc3d547e3b88bb 사용자의 3번째 일기 삭제")
    void deleteDiary() {
        //given
        Diary diary = diaryRepository.findAllByUserId("63c0cb6f30dc3d547e3b88bb").get(2);

        //when
        diaryRepository.delete(diary);

        //then
        assertEquals(diaryRepository.findAll().size(), 19);
        for (Diary tmp : mongoTemplate.findAll(Diary.class, "diary")) {
            System.out.println(tmp.toString());
        }
    }

    @Test
    @Order(3)
    @DisplayName("오늘 일기 저장 (Create)")
    void saveToday() {
        //given
        Diary diary = Diary.builder()
                .userId("63c0cb6f30dc3d547e3b88bb")
                .content("content" + " today")
                .date(LocalDate.now())
                .emotion("12")
                .keywords(new String[]{"today"})
                .thumbnail("thumbnail" + " today")
                .build();

        //when
        diaryRepository.save(diary);

        //then
        assertEquals(diaryRepository.findAll().size(), 20);
        assertTrue(diary.getContent().startsWith("content"));
        assertEquals(Arrays.stream(diary.getKeywords()).count(), 1);
    }

    @Test
    @Order(4)
    @DisplayName("일기 전체 조회 (Read)")
    void findAll() {
        //when
        //then
        assertEquals(diaryRepository.findAll().size(), 20);
        for (Diary tmp : mongoTemplate.findAll(Diary.class, "diary")) {
            System.out.println(tmp.toString());
        }
    }

    @Test
    @Order(5)
    @DisplayName("일기 조회 (Read) - ObjectId")
    void findById() {
        //given
        Diary diary = diaryRepository.findAll().get(0);
        String diaryId = diary.getId();

        //when
        Diary findDiary = diaryRepository.findById(diaryId).orElse(null);

        //then
        assertThat(findDiary)
                .extracting("id")
                .isEqualTo(diaryId);
    }

    @Test
    @Order(6)
    @DisplayName("사용자 일기 전체 조회 (Read) - User ObjectId")
    void findAllByUserId() {
        //then
        assertThat(diaryRepository.findAllByUserId("63c0cb6f30dc3d547e3b88bb").size())
                .isEqualTo(20);
        for (Diary tmp : diaryRepository.findAllByUserId("63c0cb6f30dc3d547e3b88bb")) {
            assertThat(tmp.getUserId())
                    .isEqualTo("63c0cb6f30dc3d547e3b88bb");
        }
    }

    @Test
    @Order(7)
    @DisplayName("오늘 일기 조회 (Read) - User ObjectId")
    void findDiaryByUserIdAndDate() {
        //given
        LocalDate today = LocalDate.now();
        //then
        assertThat(diaryRepository.findDiaryByUserIdAndDate("63c0cb6f30dc3d547e3b88bb", today.atTime(9,0)).get().getDate())
                .isEqualTo(LocalDate.now().atTime(9,0));
    }

    @Test
    @Order(8)
    @DisplayName("MongoTemplate_사용자 전체 일기 조회(Paging)")
    void findDiaryByUserId_MongoTemplate() {
        //given
        Pageable pageable = PageRequest.of(0 , 9, Sort.by("date"));
        Query query = new Query()
                .with(pageable)
                .skip((long) pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize());
        query.addCriteria(Criteria.where("userId").is("63c0cb6f30dc3d547e3b88bb"));

        //when
        List<DiaryInfoResponse> diaryInfoResponsesList = mongoTemplate.find(query, DiaryInfoResponse.class, "diary");

        //then
        assertThat(diaryInfoResponsesList.size()).isEqualTo(9);

        for (DiaryInfoResponse diaryInfoResponse : diaryInfoResponsesList) {
            assertThat(diaryInfoResponse.getUserId()).isEqualTo("63c0cb6f30dc3d547e3b88bb");
        }

    }

    @Test
    @Order(9)
    @DisplayName("MongoTemplate_사용자 일기 조건 검색(Paging)")
    void findDiary_MongoTemplate() {
        //given
        Pageable pageable = PageRequest.of(0 , 20, Sort.by("date"));
        Query query = new Query()
                .with(pageable)
                .skip((long) pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize());
        query.addCriteria(Criteria.where("userId").is("63c0cb6f30dc3d547e3b88bb"));
        query.addCriteria(Criteria.where("date").gte(LocalDate.parse("2023-01-10").atTime(9,0)).lte(LocalDate.parse("2023-01-15").atTime(9,0)));
//        query.addCriteria(Criteria.where("emotion").is(""));

        //when
        List<DiaryInfoResponse> diaryInfoResponsesList = mongoTemplate.find(query, DiaryInfoResponse.class, "diary");

        //then
        assertThat(diaryInfoResponsesList.size()).isEqualTo(6);
        int idx = 10;
        for (DiaryInfoResponse diaryInfoResponse : diaryInfoResponsesList) {
            assertThat(diaryInfoResponse.getEmotion()).isEqualTo(idx + "");
            assertThat(diaryInfoResponse.getUserId()).isEqualTo("63c0cb6f30dc3d547e3b88bb");
            idx++;
        }
    }

}