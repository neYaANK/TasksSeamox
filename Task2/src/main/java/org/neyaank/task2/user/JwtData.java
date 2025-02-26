/*
 * LoginResponse.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class JwtData {
    private String token;
    private int expiry;
    private String type;
    private String email;
}
