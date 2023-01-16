package com.sweep.jaksim31.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sweep.jaksim31.dto.diary.DiaryAnalysisRequest;
import com.sweep.jaksim31.dto.diary.DiaryAnalysisResponse;
import com.sweep.jaksim31.dto.diary.DiarySaveRequest;
import com.sweep.jaksim31.dto.diary.DiaryInfoResponse;
import com.sweep.jaksim31.domain.diary.Diary;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

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
 * 2023-01-09           김주현             최초 생성
 * 2023-01-12           김주현       Diary 정보 조회 Return형식을 DiaryInfoDTO로 변경
 * 2023-01-12           방근호       analyzeDiary 메소드 추가
 * 2023-01-14           김주현         todayDiary 메소드 추가
 */

public interface DiaryService {
    // 일기 전체 조회
    ResponseEntity<List<Diary>> allDiaries();

    // 사용자 일기 전체 조회
    ResponseEntity<List<DiaryInfoResponse>> findUserDiaries(String user_id);

    // 일기 생성
    ResponseEntity<Diary> saveDiary(DiarySaveRequest diarySaveRequest);

    // 일기 수정
    ResponseEntity<Diary> updateDiary(String diary_id, DiarySaveRequest diarySaveRequest);

    // 일기 삭제
    ResponseEntity<String> remove(String diary_id);

    // 일기 조회
    ResponseEntity<DiaryInfoResponse> findDiary(String diary_id);

    // 오늘 일기 조회
    String todayDiary(String userId);

    // 일기 검색
    ResponseEntity<List<DiaryInfoResponse>> findDiaries(String userId, Map<String, Object> params);

    // 일기 분석
    ResponseEntity<DiaryAnalysisResponse> analyzeDiary(DiaryAnalysisRequest diaryAnalysisRequest) throws JsonProcessingException, ParseException;


}
