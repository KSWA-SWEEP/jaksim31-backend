package com.sweep.jaksim31.domain.diary;

import com.sweep.jaksim31.domain.members.MemberRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Sort.by;

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
    @Order(3)
    @DisplayName("일기 전체 조회 (Read) - User ObjectId")
    void findAllByUserId() {
        //then
        assertThat(diaryRepository.findAllByUserId("63c0cb6f30dc3d547e3b88bb").size())
                .isEqualTo(20);
    }

    @Test
    @Order(4)
    @DisplayName("MongoTemplate")
    void findDiaryByUserIdAndDate() {
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
}