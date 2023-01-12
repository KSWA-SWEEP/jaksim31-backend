package com.sweep.jaksim31.controller;

import com.sweep.jaksim31.dto.diary.DiaryDTO;
import com.sweep.jaksim31.entity.diary.Diary;
import com.sweep.jaksim31.service.impl.DiaryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
 * 2023-01-09           김주현             최초 생성
 * 2023-01-10           김주현             개별 일기 조회 추가
 * 2023-01-11           김주현             Tag 추가 및 일기 존재 여부 확인 api 추가
 */
/* TODO
    * 일기 등록 시 최근 날짜의 일기인 경우 사용자 recent_diaries에 넣어주기 -> Members Entity 수정 후 진행해야함
    * 삭제 시 사용자 정보의 최근 일기에 해당 일기가 있는지 확인하고 있으면 삭제
* */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "일기", description = "일기 관련 api 입니다.")
@RequestMapping(path = "/v0/diaries")
public class DiaryApiController {
    private final DiaryServiceImpl diaryService;

    // 전체 일기 조회
    @Operation(summary = "전체 일기 조회", description = "모든 일기를 조회합니다.")
    @GetMapping(value = "")
    public List<Diary> allDiaries(){
        return diaryService.allDiaries();
    }

    // 일기 등록
    @Operation(summary = "일기 등록", description = "일기를 저장합니다.")
    @PostMapping(value = "")
    public Diary saveDiary(@Validated @RequestBody DiaryDTO diaryDto){
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
    @Operation(summary = "일기 수정", description = "일기를 수정합니다.")
    @PutMapping(value = "{diaryId}")
    public Diary updateDiary(@PathVariable String diaryId, @Validated @RequestBody DiaryDTO diaryDto){
        System.out.println(String.format("Diary ID \"%s\" Update",diaryId));
        return diaryService.updateDiary(diaryId, diaryDto);
    }

    // 일기 삭제
    @Operation(summary = "일기 삭제", description = "일기를 삭제합니다.")
    @DeleteMapping(value="{diaryId}")
    public String deleteDiary(@PathVariable String diaryId){
        return diaryService.remove(diaryId);
    }

    // 개별 일기 조회
    @Operation(summary = "개별 일기 조회", description = "일기ID로 하나의 일기를 조회합니다.")
    @GetMapping(value="{userId}/{diaryId}")
    public Diary findDiary(@PathVariable String userId, @PathVariable String diaryId){
        return diaryService.findDiary(diaryId);
    }

    // 사용자 일기 검색
    @Operation(summary = "사용자 일기 겁색", description = "해당 사용자의 일기를 조회합니다. 조회 조건(Query parameter)이 없을 경우 해당 사용자의 전체 일기가 조회됩니다.")
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
