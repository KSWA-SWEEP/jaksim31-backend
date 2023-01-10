package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.dto.diary.DiaryDTO;
import com.sweep.jaksim31.entity.diary.Diary;
import com.sweep.jaksim31.service.impl.DiaryServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * packageName :  com.sweep.jaksim31.controller
 * fileName : DiaryApiController
 * author :  김주현
 * date : 2023-01-09
 * description : Diary Api
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09                김주현             최초 생성
 * 2023-01-10                김주현            개별 일기 조회 추가
 */
/* TODO
    * 일기 등록 시 최근 날짜의 일기인 경우 사용자 recent_diaries에 넣어주기 -> Members Entity 수정 후 진행해야함
* */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v0/diaries")
public class DiaryApiController {
    @Autowired
    DiaryServiceImpl diaryService;

    // 전체 일기 조회
    @GetMapping(value = "")
    public List<Diary> allDiaries(@RequestParam Map<String, String> params){
        return diaryService.allDiaries();
    }

    // 일기 등록
    @PostMapping(value = "")
    public Diary saveDiary(@RequestBody DiaryDTO diaryDto){
        //확인
        // System.out.println("Diary dto = " + diaryDto.toString());

        //사용자 current_diaries에 현재 작성한 다이어리 넣기
//        ObjectId userId = new ObjectId(diaryDto.getUser_id());
//        Members user = member.findUser(new ObjectId(diaryDto.getUser_id()));
//        List<Diary> diaries = user.getRecentDiaries();
//
//        user.setRecentDiaries(diaries);
//        userService.updateUser(user);
//        diaries.add(diary);

        Diary diary = diaryService.saveDiary(diaryDto);
        return diary;
    }

    // 일기 수정
    @PutMapping(value = "{diaryId}")
    public Diary updateDiary(@PathVariable String diaryId, @RequestBody DiaryDTO diaryDto){
        System.out.println(String.format("Diary ID \"%s\" Update",diaryId));
        return diaryService.updateDiary(diaryId, diaryDto);
    }

    // 일기 삭제
    @DeleteMapping(value="{diaryId}")
    public String deleteDiary(@PathVariable String diaryId){
        return diaryService.remove(diaryId);
    }

    // 개별 일기 조회
    @GetMapping(value="{userId}/{diaryId}")
    public Diary findDiary(@PathVariable String userId, @PathVariable String diaryId){
        return diaryService.findDiary(diaryId);
    }

    // 사용자 일기 검색
    @GetMapping(value = "{userId}")
    public List<Diary> findUserDiary(@PathVariable String userId, @RequestParam(required = false) Map<String, Object> params){
        System.out.println(userId + "'s diaries");
        // 조건이 없으면 사용자 일기 전체 조회
        if(params.isEmpty()){
            return diaryService.findUserDiaries(userId);
        }else
            System.out.println("parameters : " + params.toString());
        return diaryService.findDiaries(userId, params);
    }


}
