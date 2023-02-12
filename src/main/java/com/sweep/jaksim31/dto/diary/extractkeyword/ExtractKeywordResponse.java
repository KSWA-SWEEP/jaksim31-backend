package com.sweep.jaksim31.dto.diary.extractkeyword;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sweep.jaksim31.enums.DiaryExceptionType;
import com.sweep.jaksim31.exception.BizException;
import lombok.*;

import java.util.*;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryAnalysisResponse
 * author :  방근호
 * date : 2023-01-13
 * description : Diary 키워드 추출 및 감정 분석을 위한 응답 dto
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */


@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
public class ExtractKeywordResponse {

    @JsonProperty("request_id")
    private String requestId;

    private int result;

    @JsonProperty("return_type")
    private String returnType;

    @JsonProperty("return_object")
    private Map<String, Object> returnObject;


    public Map<String, Object> extractKeyword() {

        List<Map<String, Object>> sentences = (List<Map<String, Object>>)  this.returnObject.get("sentence");

        Map<String, Morpheme> morphemesMap = new HashMap<>();
        Map<String, NameEntity> nameEntitiesMap = new HashMap<>();
        List<Morpheme> morphemes = null;
        List<NameEntity> nameEntities = null;

        for( Map<String, Object> sentence : sentences ) {

            // 형태소 분석기 결과 수집 및 정렬
            List<Map<String, Object>> morphologicalAnalysisResult = (List<Map<String, Object>>) sentence.get("morp");
            for( Map<String, Object> morphemeInfo : morphologicalAnalysisResult ) {
                String lemma = (String) morphemeInfo.get("lemma");
                Morpheme morpheme = morphemesMap.get(lemma);
                if (Objects.isNull(morpheme)) {
                    morpheme = new Morpheme(lemma, (String) morphemeInfo.get("type"), 1);
                    morphemesMap.put(lemma, morpheme);
                } else {
                    morpheme.count = morpheme.count + 1;
                }
            }


            // 개체명 분석 결과 수집 및 정렬
            List<Map<String, Object>> nameEntityRecognitionResult = (List<Map<String, Object>>) sentence.get("NE");
            for( Map<String, Object> nameEntityInfo : nameEntityRecognitionResult ) {
                String name = (String) nameEntityInfo.get("text");
                NameEntity nameEntity = nameEntitiesMap.get(name);
                if ( Objects.isNull(nameEntity) ) {
                    nameEntity = new NameEntity(name, (String) nameEntityInfo.get("type"), 1);
                    nameEntitiesMap.put(name, nameEntity);
                } else {
                    nameEntity.setCount(nameEntity.getCount() + 1);
                }
            }
        }

        if ( 0 < nameEntitiesMap.size() ) {
            nameEntities = new ArrayList<>(nameEntitiesMap.values());
            nameEntities.sort( (nameEntity1, nameEntity2) -> nameEntity2.getCount() - nameEntity1.getCount());
        }

        if ( 0 < morphemesMap.size() ) {
            morphemes = new ArrayList<>(morphemesMap.values());
            morphemes.sort( (morpheme1, morpheme2) -> morpheme2.getCount() - morpheme1.getCount());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("morphemes", morphemes);
        response.put("nameEntities", nameEntities);

        // 인식된 개채명들 많이 노출된 순으로 출력 ( 최대 5개 )
        return response;
    }

    @Data
    public class NameEntity {
        final String text;
        final String type;
        Integer count;
        public NameEntity (String text, String type, Integer count) {
            this.text = text;
            this.type = type;
            this.count = count;
        }
    }

    @Data
    public class Morpheme {
        final String text;
        final String type;
        Integer count;
        public Morpheme (String text, String type, Integer count) {
            this.text = text;
            this.type = type;
            this.count = count;
        }
    }

}
