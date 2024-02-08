package com.neu.webapp.repository;

import com.neu.webapp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // find userEntity by username
    Optional<UserEntity> findByUsername(String username);

    // check if this user exists
    Boolean existsByUsername(String username);
}
