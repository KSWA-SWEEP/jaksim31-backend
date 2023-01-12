package com.sweep.jaksim31.entity.diary;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * packageName :  com.sweep.jaksim31.entity.diary
 * fileName : DiaryRepository
 * author :  김주현
 * date : 2023-01-09
 * description : Diary Collection을 위한 Repository
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           김주현             최초 생성
 * 2023-01-11           김주현             사용자 id와 날짜로 조회하는 method 추가
 */

public interface DiaryRepository extends MongoRepository<Diary, String> {
    List<Diary> findAll();
    Optional<Diary> findById(ObjectId id);

    List<Diary> findAllByUserId(ObjectId user_id);
    Optional<Diary> findDiaryByUserIdAndDate(ObjectId user_id, LocalDateTime date);

    List<Diary> findDiariesByUserIdAndEmotionAndDateBetween(ObjectId user_id, String emotion, LocalDateTime date, LocalDateTime date2);
    List<Diary> findDiariesByUserIdAndDateBetween(ObjectId user_id, LocalDateTime date, LocalDateTime date2);
}

