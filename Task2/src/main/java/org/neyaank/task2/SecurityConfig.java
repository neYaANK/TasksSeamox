/*
 * SecurityConfig.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    /**
     * Spring Security Endpoint Protection Configuration
     * Right now simply makes all requests authorized as we don't have Auth
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(request->
                request.anyRequest().permitAll()).build();
    }
}
