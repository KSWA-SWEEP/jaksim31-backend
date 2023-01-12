package com.sweep.jaksim31.service.impl;

import com.sweep.jaksim31.dto.diary.DiaryDTO;
import com.sweep.jaksim31.entity.diary.Diary;
import com.sweep.jaksim31.entity.diary.DiaryRepository;
import com.sweep.jaksim31.entity.members.MemberRepository;
import com.sweep.jaksim31.service.DiaryService;
import com.sweep.jaksim31.util.exceptionhandler.BizException;
import com.sweep.jaksim31.util.exceptionhandler.DiaryExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Map;

/**
 * packageName :  com.sweep.jaksim31.service.impl
 * fileName : DiaryServiceImpl
 * author :  김주현
 * date : 2023-01-09
 * description : Diary Service 구현
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           김주현             최초 생성
 * 2023-01-11           김주현             ErrorHandling 추가
 */
/* TODO
    * 일기 조건 조회 MongoTemplate 사용해서 수정하기
* */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;

    @Override
    // 전체 일기 조회
    public List<Diary> allDiaries(){
        return diaryRepository.findAll();
    }

    @Override
    // 사용자 id 전체 일기 조회
    public List<Diary> findUserDiaries(String user_id){
        return diaryRepository.findAllByUserId(new ObjectId(user_id));
    }

    @Override
    // 일기 저장
    public Diary saveDiary(DiaryDTO diaryDto){
        // 사용자를 찾을 수 없을 때
        if(diaryDto.getUser_id() == null || diaryDto.getUser_id().equals("") || !memberRepository.findById(diaryDto.getUser_id()).isPresent())
            throw new BizException(DiaryExceptionType.NOT_FOUND_USER);
        // 해당 날짜에 이미 등록 된 다이어리가 있을 때
        if(diaryRepository.findDiaryByUserIdAndDate(new ObjectId(diaryDto.getUser_id()), diaryDto.getDate().atTime(9,0)).isPresent())
            throw new BizException(DiaryExceptionType.DUPLICATE_DIARY);
        // 날짜가 유효하지 않을 때(미래)
        if(diaryDto.getDate().isAfter(ChronoLocalDate.from(LocalDate.now().atTime(11,59)))){
            throw new BizException(DiaryExceptionType.WRONG_DATE);
        }
        Diary diary = diaryDto.toEntity();
        return diaryRepository.save(diary);
    }

    @Override
    // 일기 수정
    public Diary updateDiary(String diary_id, DiaryDTO diaryDTO) {
        // 사용자를 찾을 수 없을 때
        if(diaryDTO.getUser_id() == null || diaryDTO.getUser_id().equals("") || !memberRepository.findById(diaryDTO.getUser_id()).isPresent())
            throw new BizException(DiaryExceptionType.NOT_FOUND_USER);
        // 날짜가 유효하지 않을 때(미래)
        if(diaryDTO.getDate().isAfter(ChronoLocalDate.from(LocalDate.now().atTime(11,59)))){
            throw new BizException(DiaryExceptionType.WRONG_DATE);
        }
        Diary updatedDiary = new Diary(diary_id, diaryDTO);
        diaryRepository.save(updatedDiary);
        return updatedDiary;
    }

    @Override
    // 일기 삭제
    public String remove(String diary_id) {
        Diary diary = diaryRepository.findById(diary_id).orElseThrow(() -> new IllegalArgumentException(String.format("[service] Diary id \"%s\" not exist!!", diary_id)));
        diaryRepository.delete(diary);
        return diary.getId().toString();
    }

    @Override
    // 일기 조회
    public Diary findDiary(String diary_id) {
        return diaryRepository.findById(new ObjectId(diary_id)).orElseThrow(() -> new IllegalArgumentException(String.format("[service] Diary id \"%s\" not exist!!", diary_id)));
    }

    @Override
    // 일기 검색
    public List<Diary> findDiaries(String userId, Map<String, Object> params){
        // Repository 방식
        List<Diary> diaries;
        LocalDateTime start_date;
        LocalDateTime end_date;
        // 시간 조건 설정
        if(params.keySet().contains("startDate"))
            start_date = (LocalDate.parse(((String)params.get("startDate")))).atTime(9,0);
        else
            start_date = LocalDate.of(1990, 1, 1).atTime(9, 0);
        if(params.keySet().contains("endDate"))
            end_date = (LocalDate.parse(((String)params.get("endDate")))).atTime(9,0);
        else
            end_date = LocalDate.now().atTime(9,0);
        // 조건으로 조회
        if(params.keySet().contains("emotion"))
            diaries = diaryRepository.findDiariesByUserIdAndEmotionAndDateBetween(new ObjectId(userId), (String) params.get("emotion"), start_date, end_date);
        else
            diaries = diaryRepository.findDiariesByUserIdAndDateBetween(new ObjectId(userId), start_date, end_date);
        System.out.println("Diaries : " + diaries);

        return diaries;
    }
}
