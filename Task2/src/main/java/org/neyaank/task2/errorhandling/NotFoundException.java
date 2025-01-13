/*
 * NotFoundException.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
