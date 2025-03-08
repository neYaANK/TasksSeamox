/*
 * UserRepository.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(int id);
    Optional<User> findByVerificationCode(String verificationCode);
    List<User> findAllByVerificationStartTimeLessThanEqualAndVerifiedFalse(LocalDateTime startTime);
    Optional<User> findByEmail(String email);
}
