/*
 * VerificationTest.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.neyaank.task2.AbstractTest;
import org.neyaank.task2.user.User;
import org.springframework.http.MediaType;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Slf4j
public class VerificationTest extends AbstractTest {

    @AfterEach
    public void tearDown(){
        userRepository.deleteAll();
        userRepository.flush();
    }


    @Test
    public void should_verifyUser_whenVerifyValidCode() throws Exception {
        given_validUserDTO();
        String code = "fc0e5f6a-0b6e-4cbe-8db6-fbf6f6a8c16f";
        User user = userMapper.userDTOToUser(userDTO);
        user.setId(null);
        user.setVerificationCode(code);
        user = userRepository.save(user);
        log.info("Test verifyUser");

        mockMvc.perform(
                        get("/verification?code={code}",code))
                .andExpect(status().isOk());
        user = userRepository.findById(user.getId()).get();
        Assertions.assertEquals(user.isVerified(), true);
    }

    @Test
    public void should_return404_whenVerifyNonExistentCode() throws Exception {
        given_validUserDTO();
        String code = "fc0e5f6a-0b6e-4cbe-8db6-fbf6f6a8c16f";
        User user = userMapper.userDTOToUser(userDTO);
        user.setId(null);
        user.setVerificationCode(code);
        user = userRepository.save(user);
        log.info("Test verifyUser fail");

        mockMvc.perform(
                        get("/verification?code=abcdabcd"))
                .andExpect(status().isNotFound());
    }
}
