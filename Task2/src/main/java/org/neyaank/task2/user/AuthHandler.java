/*
 * AuthHandler.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthHandler{
    private final AuthService authService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        authService.loginSuccess(success.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent failures) {
        authService.loginFailed(failures.getAuthentication().getName());
    }

    @EventListener
    public void onLocked(AuthenticationFailureLockedEvent locked){
        User user = authService
                .tryUnlock(locked.getAuthentication().getName());
        // Can't handle custom Exception, so now it returns 401 if account's locked
        // instead of 403
        authService.authenticate(locked.getAuthentication());
    }

}
