package com.neu.webapp.repository;

import com.neu.webapp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // find userEntity by username
    Optional<UserEntity> findByUsername(String username);

    // check if this user exists
    Boolean existsByUsername(String username);
    UserEntity findByVerificationToken(String token);
}
