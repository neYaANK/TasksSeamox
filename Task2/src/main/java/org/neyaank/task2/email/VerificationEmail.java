/*
 * SendMailPojo.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerificationEmail {
    private String recipientEmail;
    private int userId;
    private String message;
}
