package com.sweep.jaksim31.entity.members;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MemberRepository extends MongoRepository<Members, String> {
    Optional<Members> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByID(String user_id);
}