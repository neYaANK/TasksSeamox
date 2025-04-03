/*
 * SendMailPojo.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class SendMailPojo {
    private String destination;
    private int userId;
    private String verificationCode;
    private String message;
}
