/*
 * EmailService.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

public interface EmailService {
    void sendVerificationEmail(String to, String unique);
}
