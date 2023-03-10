package com.sweep.jaksim31.domain.diary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * packageName :  com.sweep.jaksim31.domain.diary
 * fileName : DiarySearchQueryRepository
 * author :  김주현
 * date : 2023-02-07
 * description : 일기 조건 검색을 위한 Repository(Elastic Search)
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-02-07              김주현             최초 생성
 * 2023-02-11              김주현             날짜 오류 수정
 *                                           페이징 오류 수정
 */
@Repository
@RequiredArgsConstructor
public class DiarySearchQueryRepository {
    private static final String DIARY_INDEX = "jaksim31.diary";
    String[] params = {"userId", "startDate", "endDate", "searchWord", "emotion"};
    private final ElasticsearchOperations operations;

    @Getter
    @Setter
    public class DiarySearchResponse{
        private List<DiarySearch> diaries;
        private int size;
    }

    public DiarySearchResponse findByCondition(String userId, Map<String,Object> searchCondition, Pageable pageable) {

        CriteriaQuery query = createConditionCriteriaQuery(userId, searchCondition).setPageable(pageable);
        IndexCoordinates index = IndexCoordinates.of(DIARY_INDEX);
        SearchHits<DiarySearch> search = operations.search(query, DiarySearch.class, index);

        DiarySearchResponse result = new DiarySearchResponse();
        List<DiarySearch> searchedDiaries = search.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        result.setDiaries(searchedDiaries);
        result.setSize((int)search.getTotalHits());

        return result;
    }

    private CriteriaQuery createConditionCriteriaQuery(String userId, Map<String,Object> searchCondition) {
        CriteriaQuery query = new CriteriaQuery(new Criteria());

        if (searchCondition == null)
            return query;

        // 사용자 id 검색 조건 설정
        if (Objects.nonNull(userId))
            query.addCriteria(Criteria.where(params[0]).is(userId));
        // 날짜 검색 조건 설정
        if(!searchCondition.containsKey(params[1]))
            searchCondition.put(params[1],"1990-01-01");
        if(!searchCondition.containsKey(params[2]))
            searchCondition.put(params[2], LocalDate.now().toString());
        query.addCriteria(Criteria.where("date")
                .greaterThanEqual(LocalDate.parse((searchCondition.get(params[1]).toString())).atTime(0,0))
                .lessThanEqual(LocalDate.parse((searchCondition.get(params[2]).toString())).atTime(0,0)));
        // 검색어 조건 설정
        if(searchCondition.containsKey(params[3])) {
            query.addCriteria(Criteria.where("content").is(searchCondition.get(params[3]).toString()));
        }
        // 감정 조건 설정
        if(searchCondition.containsKey(params[4])) {
            query.addCriteria(Criteria.where(params[4]).is(searchCondition.get(params[4]).toString()));
        }

        return query;
    }
}
