/*
 * UserRepository.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(int id);
    Optional<User> findByVerificationCode(String verificationCode);
    Optional<User> findByEmail(String email);
    @Modifying
    @Query("DELETE FROM User " +
            "WHERE verified = FALSE " +
            "AND verificationStartTime <= :startTime")
    int deleteUnverifiedAndOldEnough(LocalDateTime startTime);

}
