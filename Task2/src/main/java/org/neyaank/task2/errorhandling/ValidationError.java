/*
 * ValidationError.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.errorhandling;

import lombok.Data;

@Data
public class ValidationError {
    private final String fieldName;

    private final String message;
}
