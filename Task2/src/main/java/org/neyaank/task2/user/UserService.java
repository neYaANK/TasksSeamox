/*
 * UserService.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

public interface UserService {
    User registerUser(User user);
    User updateUser(int id, User user);
    User findUserById(int id);
    User getProxyById(int id);
}
