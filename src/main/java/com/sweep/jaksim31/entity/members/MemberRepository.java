package com.sweep.jaksim31.entity.members;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

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
 */

public interface MemberRepository extends MongoRepository<Members, String> {
    Optional<Members> findById(ObjectId id);
    Optional<Members> findMembersByLoginId(String loginId);
    boolean existsById(ObjectId id);
    boolean existsByLoginId(String loginId);
}