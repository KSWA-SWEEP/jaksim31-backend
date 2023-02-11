package com.sweep.jaksim31.dto.diary.extractkeyword;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public List<NameEntity> extractKeyword() {

        Map<String, Object> returnObject = this.returnObject;
        List<Map> sentences = (List<Map>) returnObject.get("sentence");

        Map<String, NameEntity> nameEntitiesMap = new HashMap<String, NameEntity>();
        List<NameEntity> nameEntities = null;

        for( Map<String, Object> sentence : sentences ) {
            // 개체명 분석 결과 수집 및 정렬
            List<Map<String, Object>> nameEntityRecognitionResult = (List<Map<String, Object>>) sentence.get("NE");
            for( Map<String, Object> nameEntityInfo : nameEntityRecognitionResult ) {
                String name = (String) nameEntityInfo.get("text");
                NameEntity nameEntity = nameEntitiesMap.get(name);
                if ( nameEntity == null ) {
                    nameEntity = new NameEntity(name, (String) nameEntityInfo.get("type"), 1);
                    nameEntitiesMap.put(name, nameEntity);
                } else {
                    nameEntity.setCount(nameEntity.getCount() + 1);
                }
            }
        }


        if ( 0 < nameEntitiesMap.size() ) {
            nameEntities = new ArrayList<NameEntity>(nameEntitiesMap.values());
            nameEntities.sort( (nameEntity1, nameEntity2) -> {
                return nameEntity2.getCount() - nameEntity1.getCount();
            });
        }

        // 인식된 개채명들 많이 노출된 순으로 출력 ( 최대 5개 )
        return nameEntities;
    }

}
