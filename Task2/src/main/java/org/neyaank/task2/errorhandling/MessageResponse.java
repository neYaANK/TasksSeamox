/*
 * MessageResponse.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MessageResponse {
    private String message;
    public MessageResponse(String message) {
        this.message = message;
    }
}
