/*
 * JwtTokenGenerator.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenGenerator {
    private final JwtEncoder jwtEncoder;
    private final int expireMinutes = 60;

    public JwtData generateAccessToken(Authentication authentication) {

        log.debug("generateAccessToken token creation started for:{}", authentication.getName());
        JwtData data = new JwtData();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("task2app")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(expireMinutes , ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .build();
        JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
        String token = jwtEncoder
                .encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
        data.setToken(token);
        data.setType("Bearer");
        data.setExpiry(expireMinutes);
        data.setEmail(authentication.getName());
        return data;
    }

}
