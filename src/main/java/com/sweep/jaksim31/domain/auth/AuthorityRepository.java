package com.sweep.jaksim31.domain.auth;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuthorityRepository extends MongoRepository<Authority,String> {
    Optional<Authority> findByAuthorityName(MemberAuth authorityName);
}
