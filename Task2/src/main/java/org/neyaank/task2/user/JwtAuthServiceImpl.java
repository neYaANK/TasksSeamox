/*
 * AuthService.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neyaank.task2.errorhandling.UserNotVerifiedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtAuthServiceImpl implements AuthService {
    private final JwtTokenGenerator generator;
    private final UserService userService;
    @Value("${neya.login.attempts}")
    private int MAX_ATTEMPTS;

    @Override
    public JwtData authenticate(Authentication auth) {
        User user = userService.findUserByEmail(auth.getName());
        JwtData jwt = generator.generateAccessToken(auth);
        return jwt;
    }

    @Override
    public void loginSuccess(String email) {
        User user = userService.resetFailedAttempts(email);
    }

    @Override
    public void loginFailed(String email) {
        log.debug("Login failed for {}", email);
        User user = userService.incrementFailedAttempts(email);
        if(user.getFailedAttempts() == MAX_ATTEMPTS){
            user = userService.resetFailedAttempts(email);
            user = userService.lockAccount(email);
        }
    }

    @Override
    public User tryUnlock(String email) {
        log.debug("Trying to unlock {}...", email);
        User user = userService.findUserByEmail(email);
        if(user.getUnlockTime().isBefore(LocalDateTime.now())){
            user = userService.unlockAccount(email);
            return user;
        }
        log.debug("Account {} is locked till {}", email, user.getUnlockTime().toString());
        return user;
    }

}
