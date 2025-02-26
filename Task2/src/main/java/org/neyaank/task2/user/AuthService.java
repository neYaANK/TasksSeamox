/*
 * AuthService.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import org.springframework.security.core.Authentication;

public interface AuthService {
    JwtData authenticate(Authentication auth);
}
