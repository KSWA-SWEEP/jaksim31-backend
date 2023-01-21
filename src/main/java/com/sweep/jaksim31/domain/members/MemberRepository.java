package com.sweep.jaksim31.domain.members;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * packageName :  com.sweep.jaksim31.entity.members
 * fileName : MemberRepository
 * author :  방근호
 * date : 2023-01-09
 * description : Member Collection을 위한 Repository
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             조회 조건 수정 및 추가
 * 2023-01-18           김주현             id data type 변경(ObjectId -> String)
 * 2023-01-21           방근호             existsByLoginId 메소드 type 변경(Optional<Members> -> boolean
 * 2023-01-21           방근호             findMemberByLoginId 이름 변경 (-> findByLoginId)
 */

public interface MemberRepository extends MongoRepository<Members, String> {
    Optional<Members> findById(String id);
    Optional<Members> findByLoginId(String loginId);
    boolean existsById(String id);
    boolean existsByLoginId(String loginId);
}