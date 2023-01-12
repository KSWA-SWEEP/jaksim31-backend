package com.sweep.jaksim31.entity.token;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
/**
 * packageName :  com.sweep.jaksim31.entity.token
 * fileName : RefreshTokenRepository
 * author :  방근호
 * date : 2023-01-09
 * description : Refresh-Token Collection을 위한 Repository
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 */

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByLoginId(String loginId);

    void deleteByLoginId(String loginId);
}