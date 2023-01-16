package com.sweep.jaksim31.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweep.jaksim31.controller.feign.*;
import com.sweep.jaksim31.controller.feign.config.UploadImageFeignConfig;
import com.sweep.jaksim31.dto.diary.*;
import com.sweep.jaksim31.dto.tokakao.EmotionAnalysisRequest;
import com.sweep.jaksim31.dto.tokakao.ExtractedKeywordResponse;
import com.sweep.jaksim31.dto.tokakao.TranslationRequest;
import com.sweep.jaksim31.dto.tokakao.TranslationResponse;
import com.sweep.jaksim31.domain.diary.Diary;
import com.sweep.jaksim31.domain.diary.DiaryRepository;
import com.sweep.jaksim31.domain.members.MemberRepository;
import com.sweep.jaksim31.exception.type.MemberExceptionType;
import com.sweep.jaksim31.exception.type.ThirdPartyExceptionType;
import com.sweep.jaksim31.service.DiaryService;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.DiaryExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
 * 2023-01-12           김주현             Diary 정보 조회 Return형식을 DiaryInfoDTO로 변경
 * 2023-01-13           방근호             일기 분석 및 썸네일 업로드 기능 추가
 * 2023-01-14           김주현             오늘 일기 조회 기능 추가
 * 2023-01-16           방근호             모든 메소드 리턴값 수정(ResponseEntity 사용)
 * 2023-01-16           방근호             써드파티 api 이용 예외처리 추가
 */
/* TODO
    * 일기 조건 조회 MongoTemplate 사용해서 수정하기
    * API 호출 시 에러 핸들링 하는 코드 추가 작성 해야 함
*/
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'");

    @Value("${kakao.download-storage.url}")
    private String DOWNLOAD_URL;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final UploadImageFeign uploadImageFeign;
    private final DownloadImageFeign downloadImageFeign;
    private final KakaoApiTokenRefreshFeign apiTokenRefreshFeign;
    private final ExtractKeywordFeign extractKeywordFeign;
    private final TranslationFeign translationFeign;
    private final EmotionAnalysisFeign emotionAnalysisFeign;

    @Override
    // 전체 일기 조회
    public ResponseEntity<List<Diary>> allDiaries(){
        return ResponseEntity.ok(diaryRepository.findAll());
    }

    @Override
    // 사용자 id 전체 일기 조회
    public ResponseEntity<List<DiaryInfoResponse>> findUserDiaries(String userId){

        memberRepository.findById(userId)
                .orElseThrow(()-> new BizException(MemberExceptionType.NOT_FOUND_USER));

        return ResponseEntity.ok(diaryRepository.findAllByUserId(new ObjectId(userId)).stream()
                .map(DiaryInfoResponse::of)
                .collect(Collectors.toList()));
    }

    /**
     * @title saveDiary (일기 저장)
     * @param diarySaveRequest
     * @return Diary
     */
    @Override
    public ResponseEntity<Diary> saveDiary(DiarySaveRequest diarySaveRequest){
        // 사용자를 찾을 수 없을 때
        memberRepository
                .findById(diarySaveRequest.getUserId())
                .orElseThrow(()-> new BizException(MemberExceptionType.NOT_FOUND_USER));
        // 해당 날짜에 이미 등록 된 다이어리가 있을 때
        diaryRepository
                .findDiaryByUserIdAndDate(new ObjectId(diarySaveRequest.getUserId()), diarySaveRequest.getDate().atTime(9,0))
                .orElseThrow(() -> new BizException(DiaryExceptionType.DUPLICATE_DIARY));

        // 날짜가 유효하지 않을 때(미래)
        if(diarySaveRequest.getDate().isAfter(ChronoLocalDate.from(LocalDate.now().atTime(11,59)))){
            throw new BizException(DiaryExceptionType.WRONG_DATE);
        }
        Diary diary = diarySaveRequest.toEntity();
        // 썸네일 URL 추가
        diary.setThumbnail(DOWNLOAD_URL+"/" + diary.getUserId() + "/" + DATE_FORMATTER.format(ZonedDateTime.now()) + "_r_640x0_100_0_0.png");
        return new ResponseEntity<>(diaryRepository.save(diary), HttpStatus.CREATED);
    }

    /**
     *  updateDiary 일기 수정
     * @param diaryId
     * @param diarySaveRequest
     * @return Diary
     */
    @Override
    @Transactional
    public ResponseEntity<Diary> updateDiary(String diaryId, DiarySaveRequest diarySaveRequest) {
        // 사용자를 찾을 수 없을 때
        memberRepository
                .findById(diarySaveRequest.getUserId())
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
        // 날짜가 유효하지 않을 때(미래)
        if(diarySaveRequest.getDate().isAfter(ChronoLocalDate.from(LocalDate.now().atTime(11,59)))){
            throw new BizException(DiaryExceptionType.WRONG_DATE);
        }

        Diary updatedDiary = new Diary(diaryId, diarySaveRequest);
        return ResponseEntity.ok(diaryRepository.save(updatedDiary));
    }

    @Override
    // 일기 삭제
    public ResponseEntity<String> remove(String diary_id) {
        Diary diary = diaryRepository
                .findById(diary_id)
                .orElseThrow(() -> new BizException(DiaryExceptionType.DELETE_NOT_FOUND_DIARY));
        diaryRepository.delete(diary);
        return ResponseEntity.ok(diary.getId().toString());
    }

    @Override
    // 일기 조회
    public ResponseEntity<DiaryInfoResponse> findDiary(String diary_id) {
        return ResponseEntity.ok(
                DiaryInfoResponse
                        .of(diaryRepository.findById(new ObjectId(diary_id))
                                .orElseThrow(() -> new BizException(DiaryExceptionType.NOT_FOUND_DIARY))));
    }


    @Override
    // 오늘 일기 조회
    public String todayDiary(String userId){
        LocalDate today = LocalDate.now();
        Diary todayDiary = diaryRepository.findDiaryByUserIdAndDate(new ObjectId(userId), today.atTime(9,0)).orElseThrow(() -> new BizException(DiaryExceptionType.NOT_FOUND_DIARY));
        return todayDiary.getId().toString();
    }
    
    /**
     * @param userId 유저 아이디
     * @param params 정렬 조건
     * @return List of DiaryInfoResponse
     */

    @Override
    // 일기 검색, 조건 조회
    public ResponseEntity<List<DiaryInfoResponse>> findDiaries(String userId, Map<String, Object> params){

        // Repository 방식
        List<DiaryInfoResponse> diaries;
        LocalDateTime start_date;
        LocalDateTime end_date;
        // 시간 조건 설정
        if(params.containsKey("startDate"))
            start_date = (LocalDate.parse(((String)params.get("startDate")))).atTime(9,0);
        else
            start_date = LocalDate.of(1990, 1, 1).atTime(9, 0);
        if(params.containsKey("endDate"))
            end_date = (LocalDate.parse(((String)params.get("endDate")))).atTime(9,0);
        else
            end_date = LocalDate.now().atTime(9,0);
        // 조건으로 조회
        if(params.containsKey("emotion")) {
            new DiaryInfoResponse();
            diaries = diaryRepository
                    .findDiariesByUserIdAndEmotionAndDateBetween(new ObjectId(userId), (String) params.get("emotion"), start_date, end_date)
                    .stream()
                    .map(DiaryInfoResponse::of)
                    .collect(Collectors.toList());
        } else {
            new DiaryInfoResponse();
            diaries = diaryRepository
                    .findDiariesByUserIdAndDateBetween(new ObjectId(userId), start_date, end_date)
                    .stream()
                    .map(DiaryInfoResponse::of)
                    .collect(Collectors.toList());
        }
        System.out.println("Diaries : " + diaries);

        return ResponseEntity.ok(diaries);
    }

    /**
     * @title saveThumbnail
     * @param diaryThumbnailRequest
     * @return 200 OK
     * @throws URISyntaxException
     */

    @Transactional
    public ResponseEntity<String> saveThumbnail(DiaryThumbnailRequest diaryThumbnailRequest) throws URISyntaxException {
        String userId = diaryThumbnailRequest.getUserId();
        String url = diaryThumbnailRequest.getThumbnail();
        byte[] image = downloadImageFeign.getImage(new URI(url)).getBody();

        try {
            uploadImageFeign.uploadFile("/" + userId + "/" + DATE_FORMATTER.format(ZonedDateTime.now()) + ".png", image);
            return ResponseEntity.ok().body("객체 스토리지에 업로드 성공");

        } catch (Exception e){
            // 인증 API 토큰 발급 후 추출
            ResponseEntity<String> tokenResponse = apiTokenRefreshFeign.refreshApiToken();
            HttpHeaders responseHeaders = tokenResponse.getHeaders();
            // 오브젝트 스토리지에 연결 요청 시 새로 받은 인증 API 토큰 적용R
            UploadImageFeignConfig.authToken = Objects.requireNonNull(responseHeaders.get("x-subject-token")).get(0);
            // 재업로드
            ResponseEntity<Object> res = uploadImageFeign.uploadFile("/" + userId  + "/" + DATE_FORMATTER.format(ZonedDateTime.now()) + ".png", image);
            if(!res.getStatusCode().equals(HttpStatus.CREATED))
                throw new BizException(ThirdPartyExceptionType.NOT_UPLOAD_IMAGE);
//            System.out.println(UPLOAD_URL+"/" +userId+ "/" + DATE_FORMATTER.format(ZonedDateTime.now()) + ".png");
            return ResponseEntity.ok().body("객체 스토리지에 업로드 성공");
        } finally {
            System.out.println(DOWNLOAD_URL+"/" +userId + "/" + DATE_FORMATTER.format(ZonedDateTime.now()) + "_r_640x0_100_0_0.png");
        }
    }

    /**
     * 일기 내용에 대한 키워드를 추출하고, 일기에 대한 감정분석 리턴
     * @param diaryAnalysisRequest 일기분석요청 DTO
     * @return DiaryAnalysisResponse
     * @throws JsonProcessingException Json processing 예외
     * @throws ParseException parsing 예외
     */
    @Override
    public ResponseEntity<DiaryAnalysisResponse> analyzeDiary(DiaryAnalysisRequest diaryAnalysisRequest) throws JsonProcessingException, ParseException {

        List<String> koreanKeywords;
        List<String> englishKeywords = new ArrayList<>();
        String koreanEmotion;
        String englishEmotion;

        // 감정분석 (요청 보내고 -> 응답 받아와서 Json Parsing 후 -> korean emotion에 저장
        JSONParser jsonParser = new JSONParser();
        ObjectMapper mapper = new ObjectMapper();

        EmotionAnalysisRequest emotionAnalysisRequest = new EmotionAnalysisRequest(diaryAnalysisRequest.getSentences().get(0));
        ResponseEntity<JSONObject> emotionAnalysisResult = emotionAnalysisFeign.emotionAnalysis(emotionAnalysisRequest);

        // 감정 분석 api 호출 예외처리
        if(!emotionAnalysisResult.getStatusCode().equals(HttpStatus.OK)){
            throw new BizException(ThirdPartyExceptionType.NOT_ANALYZE_EMOTION);
        }

        // JSON parsingß
        String jsonStr = mapper.writeValueAsString(Objects.requireNonNull(emotionAnalysisResult.getBody()).get("0"));
        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonStr);
        JSONObject currentEmotion = (JSONObject) jsonObject.get("emotion");
        koreanEmotion = currentEmotion.get("value").toString();

        // 문장 번역
        // TranslationRequest 하나의 객체 가지고 두번의 api 요청 호출 (일기 내용, 감정)
        TranslationRequest translationRequest = new TranslationRequest();
        translationRequest.setQ(diaryAnalysisRequest.getSentences().get(0));
        ResponseEntity<TranslationResponse> translationResponse = translationFeign.translation(translationRequest);

        // 번역 api 호출 예외처리
        if(!translationResponse.getStatusCode().equals(HttpStatus.OK)){
            throw new BizException(ThirdPartyExceptionType.NOT_TRANSLATE_KEYWORD);
        }

        translationRequest.setQ(koreanEmotion);
        ResponseEntity<TranslationResponse> emotionTranslationResponse = translationFeign.translation(translationRequest);

        // 번역 api 호출 예외 처리
        if(!emotionTranslationResponse.getStatusCode().equals(HttpStatus.OK)){
            throw new BizException(ThirdPartyExceptionType.NOT_TRANSLATE_KEYWORD);
        }

        englishEmotion = Objects.requireNonNull(emotionTranslationResponse.getBody()).getOutput().get(0).get(0);

        // 키워드 추출
        DiaryAnalysisRequest translatedSentence = new DiaryAnalysisRequest();

        // 영어로 번역된 문장들을 자릅니다.
        translatedSentence.setSentences(Objects.requireNonNull(translationResponse.getBody()).getOutput().get(0));

        // 키워드 추출 예외처리
        ResponseEntity<ExtractedKeywordResponse> extractKeywords = extractKeywordFeign.extractKeyword(translatedSentence);
        if(!extractKeywords.getStatusCode().equals(HttpStatus.OK)){
            throw new BizException(ThirdPartyExceptionType.NOT_EXTRACT_KEYWORD);
        }


        for (ExtractedKeywordResponse.Result result : Objects.requireNonNull(extractKeywords.getBody()).getResult()) {
//            System.out.println(result.toString());
            // weight가 0.5 이상인 키워드만 가져온다.
            if (result.getWeight() >= 0.5)
                englishKeywords.add(result.getKeyword());
        }

        // 영어로 추출된 키워드 한글로 번역
        TranslationRequest tmpTranslationRequest = new TranslationRequest();
        tmpTranslationRequest.setSource_lang("en");
        tmpTranslationRequest.setTarget_lang("ko");

        // 번역 api 호출 전 스트링 하나로 합쳐준다.
        StringBuilder sb = new StringBuilder();
        for (ExtractedKeywordResponse.Result result : extractKeywords.getBody().getResult()) {
            sb.append(result.getKeyword()).append(", ");
        }

        // 번역 api 호출
        tmpTranslationRequest.setQ(sb.toString());
        ResponseEntity<TranslationResponse> tmpTranslationResponse = translationFeign.translation(tmpTranslationRequest);
        if(!tmpTranslationResponse.getStatusCode().equals(HttpStatus.OK)){
            throw new BizException(ThirdPartyExceptionType.NOT_TRANSLATE_KEYWORD);
        }

        koreanKeywords = Arrays.asList(Objects.requireNonNull(tmpTranslationResponse.getBody()).getOutput().get(0).get(0).split(","));

        // 응답 생성
        return ResponseEntity.ok(new DiaryAnalysisResponse(koreanKeywords, englishKeywords, koreanEmotion, englishEmotion));
    }
}
