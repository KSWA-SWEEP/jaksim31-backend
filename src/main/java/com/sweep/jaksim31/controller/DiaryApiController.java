package com.sweep.jaksim31.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sweep.jaksim31.dto.diary.*;
import com.sweep.jaksim31.dto.diary.validator.DiaryAnalysisRequestValidator;
import com.sweep.jaksim31.dto.diary.validator.DiarySaveRequestValidator;
import com.sweep.jaksim31.dto.diary.validator.DiaryThumbnailRequestValidator;
import com.sweep.jaksim31.enums.MemberExceptionType;
import com.sweep.jaksim31.enums.SuccessResponseType;
import com.sweep.jaksim31.exception.handler.ErrorResponse;
import com.sweep.jaksim31.enums.DiaryExceptionType;
import com.sweep.jaksim31.service.impl.DiaryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.net.URISyntaxException;
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
 * 2023-01-12           김주현             Diary 정보 조회 Return형식을 DiaryInfoDTO로 변경
 * 2023-01-12           방근호             Diary 컨트롤러에 클라우드와 api 통신 및 사진 다운로드 & 업로드
 * 2023-01-14           김주현             오늘 일기 조회 추가(서비스 확인용_주석 처리)
 * 2023-01-16           방근호             모든 컨트롤러에 ResponseEntity Wrapper class 사용
 * 2023-01-17           김주현             감정 통계 추가
 * 2023-01-17           김주현             사용자 일기 조회 Paging 추가
 * 2023-01-18           김주현             id data type 변경(ObjectId -> String) 및 일기 분석 method 명 수정
 * 2023-01-19           김주현             Return 타입 변경(Diary -> DiaryResponse)
 * 2023-01-20           김주현             findDiary input 값에 userId 추가
 *                      김주현             일기 삭제 api path 및 input 값에 userId 추가
 * 2023-01-21           김주현             Validation 추가
 * 2023-01-26           김주현             사용자 일기 조회 조건에 searchWord(검색어) 추가
 * 2023-02-01           김주현             PathValue(ObjectId_diaryId,userId) validation 추가
*/

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "일기", description = "일기 관련 api 입니다.")
@RequestMapping(path = "/api/v1/diaries")
@Validated
public class DiaryApiController {
    private final DiaryServiceImpl diaryService;
    private final String idPattern = "^[a-zA-Z0-9]{24}$";
    @InitBinder
    public void init(WebDataBinder binder) {
        binder.addValidators(new DiarySaveRequestValidator(), new DiaryThumbnailRequestValidator(), new DiaryAnalysisRequestValidator());
    }
    // 전체 일기 조회
    @Operation(summary = "전체 일기 조회", description = "모든 일기를 조회합니다.")
    @GetMapping(value = "")
    public ResponseEntity<List<DiaryResponse>> allDiaries(){
        return ResponseEntity.ok(diaryService.allDiaries());
    }

    // 일기 등록
    @Operation(summary = "일기 등록", description = "일기를 저장합니다.")
    @PostMapping(value = "")
    public ResponseEntity<?> saveDiary(HttpServletResponse response, @Validated @RequestBody DiarySaveRequest diarySaveRequest){
        return new ResponseEntity<>(diaryService.saveDiary(response, diarySaveRequest), SuccessResponseType.DIARY_SAVE_SUCCESS.getHttpStatus());
    }

    // 일기 수정
    @Operation(summary = "일기 수정", description = "일기를 수정합니다.")
    @PutMapping(value = "{diaryId}")
    public ResponseEntity<?> updateDiary(@Pattern(regexp = idPattern, message = "잘못 된 ID 값입니다.") @PathVariable String diaryId, @Valid @RequestBody DiarySaveRequest diarySaveRequest){
        System.out.printf("Diary ID \"%s\" Update%n",diaryId);
        return new ResponseEntity<>(diaryService.updateDiary(diaryId, diarySaveRequest), SuccessResponseType.DIARY_UPDATE_SUCCESS.getHttpStatus());
    }

    // 일기 삭제
    @Operation(summary = "일기 삭제", description = "일기를 삭제합니다.")
    @DeleteMapping(value="{userId}/{diaryId}")
    public ResponseEntity<?> deleteDiary(HttpServletResponse response, @Pattern(regexp = idPattern)@PathVariable String userId, @Pattern(regexp = idPattern)@PathVariable String diaryId){
        return new ResponseEntity<>(diaryService.remove(response, userId, diaryId), SuccessResponseType.DIARY_REMOVE_SUCCESS.getHttpStatus());
    }

    // 개별 일기 조회
    @Operation(summary = "개별 일기 조회", description = "일기ID로 하나의 일기를 조회합니다.")
    @GetMapping(value="{userId}/{diaryId}")
    public ResponseEntity<DiaryResponse> findDiary(@Pattern(regexp = idPattern)@PathVariable String userId, @Pattern(regexp = idPattern) @PathVariable String diaryId){
        return ResponseEntity.ok(diaryService.findDiary(userId, diaryId));
    }

    // 사용자 일기 조회
    @Operation(summary = "사용자 일기 조회", description = "해당 사용자의 일기를 조회합니다. 조회 조건(Query parameter)이 없을 경우 해당 사용자의 전체 일기가 조회됩니다.")
    @GetMapping(value = "{userId}")
    public ResponseEntity<Page<DiaryInfoResponse>> findUserDiary(@Pattern(regexp = idPattern)@PathVariable String userId, @RequestParam(required = false) String page, @RequestParam(required = false) String size, @RequestParam(required = false) String sort, @RequestParam(required = false) Map<String, Object> params){
        if(params.containsKey("emotion") || params.containsKey("startDate") || params.containsKey("endDate") || params.containsKey("searchWord")){
            // 페이징 및 정렬 외에 다른 조건이 있다면 ElasticSearch로 검색
            return ResponseEntity.ok(diaryService.findDiaries(userId, params));
        }else
            // 페이징 및 정렬 조건만 있으면 사용자 일기 전체 조회
            return ResponseEntity.ok(diaryService.findUserDiaries(userId, params));

    }

    @Operation(summary = "일기 분석", description = "해당 일기 문장들을 분석하고 결과(번역, 키워드 추출)를 반환합니다.")
    @PostMapping(value = "analyze")
    public ResponseEntity<DiaryAnalysisResponse> analyzeDiary(@Validated @RequestBody DiaryAnalysisRequest diaryAnalysisRequest) throws ParseException, JsonProcessingException {
        return ResponseEntity.ok(diaryService.analyzeDiary(diaryAnalysisRequest));
    }
    
    @Operation(summary = "감정 통계", description = "사용자 일기에 대한 감정 통계를 제공합니다.")
    @GetMapping(value = "{userId}/emotions")
    public ResponseEntity<DiaryEmotionStaticsResponse> emotionStatistics(@Pattern(regexp = idPattern)@PathVariable String userId,  @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = false) Map<String, Object> params) {
        return ResponseEntity.ok(diaryService.emotionStatics(userId, params));
    }
}
