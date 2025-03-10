/*
 * UserService.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User registerUser(User user);
    User updateUser(int id, User user);
    User findUserById(int id);
    User findUserByEmail(String email);
    User getProxyById(int id);
    boolean existsById(int id);
    void verify(String code);
    void unverify(String email);
    User incrementFailedAttempts(String email);
    User resetFailedAttempts(String email);
    User lockAccount(String email);
    User unlockAccount(String email);
    int deleteUnverifiedOldUsers(int hoursAgeUntilDeletion);
    List<User> findAll(Pageable pageable);
}
