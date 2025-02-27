/*
 * AuthTest.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.neyaank.task2.AbstractTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Slf4j
public class AuthTest extends AbstractTest {
    @Autowired
    BCryptPasswordEncoder encoder;
    @AfterEach
    public void tearDown(){
        userRepository.deleteAll();
    }
    @Test
    public void should_returnJwt_when_validCredentials() throws Exception {
        given_validUserDTO();
        User user = userMapper.userDTOToUser(userDTO);
        user.setVerified(true);
        user.setId(null);
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);
        log.debug("Test login with valid credentials: {}:{}",
                user.getEmail(), userDTO.getPassword());

        login(user.getEmail(), userDTO.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void should_return401_when_invalidCredentials() throws Exception {
        given_validUserDTO();
        User user = userMapper.userDTOToUser(userDTO);
        user.setVerified(true);
        user.setId(null);
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);
        log.debug("Test login with invalid credentials: {}:{}",
                user.getEmail(), userDTO.getPassword());

        login(user.getEmail(), "InvalidPassword1212")
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void should_return403_when_userNotVerified() throws Exception {
        given_validUserDTO();
        User user = userMapper.userDTOToUser(userDTO);
        user.setId(null);
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);
        log.debug("Test login with not verified user: {}:{}",
                user.getEmail(), userDTO.getPassword());

        login(user.getEmail(), userDTO.getPassword())
                .andExpect(status().isForbidden());
    }
    @Test
    public void should_lockUser_when_tooManyInvalidAttempts() throws Exception {
        given_validUserDTO();
        User user = userMapper.userDTOToUser(userDTO);
        user.setVerified(true);
        user.setId(null);
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);
        log.debug("Test login with too many attempts: {}:{}",
                user.getEmail(), userDTO.getPassword());

        login(user.getEmail(), "invalidPassword");
        login(user.getEmail(), "invalidPassword");
        login(user.getEmail(), "invalidPassword");
        login(user.getEmail(), "invalidPassword");
        login(user.getEmail(), "invalidPassword");
        Assertions.assertTrue(
                userRepository.findById(user.getId()).get().isLocked());

    }
    public ResultActions login(String username, String password) throws Exception {
        return mockMvc.perform(get("/login")
                        .with(httpBasic(username, password))
        );
    }
}
