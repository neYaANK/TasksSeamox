/*
 * AuthService.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neyaank.task2.errorhandling.UserNotVerifiedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtAuthServiceImpl implements AuthService {
    private final JwtTokenGenerator generator;
    private final UserService userService;
    @Override
    public JwtData authenticate(Authentication auth) {
        User user = userService.findUserByEmail(auth.getName());
        if (!user.isVerified()){
            throw new UserNotVerifiedException("Can't login user " + user.getEmail()
              + " because user is not verified");
        }
        JwtData jwt = generator.generateAccessToken(auth);
        return jwt;
    }
}
