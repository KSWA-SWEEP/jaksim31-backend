package com.sweep.jaksim31.entity.token;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByEmail(String email);

    void deleteByEmail(String email);
}