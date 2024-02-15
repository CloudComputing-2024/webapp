package com.neu.webapp;

import com.neu.webapp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestH2Repository extends JpaRepository<UserEntity, Long> {
}
