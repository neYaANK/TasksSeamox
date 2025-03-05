/*
 * AccountLockedException.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.errorhandling;

import java.time.LocalDateTime;

public class AccountLockedException extends RuntimeException{
    private LocalDateTime unlockTime;
    public AccountLockedException(LocalDateTime unlockTime) {
        super("Account is locked until " + unlockTime);
    }
}
