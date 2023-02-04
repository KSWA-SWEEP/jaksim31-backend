package com.sweep.jaksim31.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sweep.jaksim31.adapter.RestPage;
import com.sweep.jaksim31.dto.diary.*;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpServletResponse;
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
 * 2023-01-12           김주현             Diary 정보 조회 Return형식을 DiaryInfoDTO로 변경
 * 2023-01-12           방근호             analyzeDiary 메소드 추가
 * 2023-01-14           김주현             todayDiary 메소드 추가
 * 2023-01-17           김주현             findUserDiaries 메소드 수정
 * 2023-01-19           김주현             Return 타입 변경(Diary -> DiaryResponse)
 * 2023-01-20           김주현             findDiary 메소드 input값에 userId 추가
 *                      김주현             일기 삭제 service input 값에 userId 추가
 * 2023-01-23           방근호             Method Return type에 ResponseEntity 제거
 * 2023-01-24           방근호             Page -> RestPage 수정
 */

public interface DiaryService {
    // 일기 전체 조회
    List<DiaryResponse> allDiaries();

    // 사용자 일기 전체 조회
    RestPage<DiaryInfoResponse> findUserDiaries(String userId, Map params);

    // 일기 생성
    String saveDiary(HttpServletResponse response, DiarySaveRequest diarySaveRequest);

    // 일기 수정
    String updateDiary(String diaryId, DiarySaveRequest diarySaveRequest);

    // 일기 삭제
    String remove(HttpServletResponse response, String userId,String diaryId);

    // 일기 조회
    DiaryResponse findDiary(String userId, String diaryId);

    // 일기 검색
    RestPage<DiaryInfoResponse> findDiaries(String userId, Map<String, Object> params);

    // 일기 분석
    DiaryAnalysisResponse analyzeDiary(DiaryAnalysisRequest diaryAnalysisRequest) throws JsonProcessingException, ParseException;


}
