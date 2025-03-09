/*
 * UserScheduler.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserScheduler {
    private final UserService userService;
    private final int UNVERIFIED_DELETION_HOURS = 24;

    @Transactional
    @Scheduled(fixedRateString = "${neya.scheduler.delay}",
            timeUnit = TimeUnit.SECONDS)
    public void scheduleUnverifiedCleaning(){
        log.debug("Unverified cleaning started");
        int affected = userService.deleteUnverifiedOldUsers
                (UNVERIFIED_DELETION_HOURS);
        log.info("{} unverified users deleted", affected);
    }
}
