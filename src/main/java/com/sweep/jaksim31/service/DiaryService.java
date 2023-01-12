package com.sweep.jaksim31.service;

import com.sweep.jaksim31.dto.diary.DiaryDTO;
import com.sweep.jaksim31.entity.diary.Diary;

import java.util.List;
import java.util.Map;

/**
 * packageName :  com.sweep.jaksim31.service
 * fileName : DiaryService
 * author :  김주현
 * date : 2023-01-09
 * description : Diary를 위해 구현되어야 하는 Services
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09                김주현             최초 생성
 */
public interface DiaryService {
    // 일기 전체 조회
    List<Diary> allDiaries();

    // 사용자 일기 전체 조회
    List<Diary> findUserDiaries(String user_id);

    // 일기 생성
    Diary saveDiary(DiaryDTO diaryDto);

    // 일기 수정
    Diary updateDiary(String diary_id, DiaryDTO diaryDTO);

    // 일기 삭제
    String remove(String diary_id);

    // 일기 조회
    Diary findDiary(String diary_id);

    // 일기 검색
    List<Diary> findDiaries(String userId, Map<String, Object> params);
}
