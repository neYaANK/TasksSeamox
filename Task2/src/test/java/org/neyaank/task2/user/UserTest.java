/*
 * UserTest.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class UserTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    private UserDTO userDTO;

    //Make tests more readable
    public void given_ValidUserDTO(){
        userDTO = new UserDTO(-1,"Pass1234$12","email@gmail.com",
                "fname", "lname", LocalDate.now(),
                "+430000000000", false);
    }
    public void should_returnBadRequest() throws Exception {
        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_returnNewUser_whenRegisterValidUser() throws Exception {
        given_ValidUserDTO();
        log.debug("Test registerValidUser user = {}", userDTO);

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "Pass$1", "passworddd", "PASSDWORDDD", "$$$$$$$$$$", "Password12",
                    "Password$$", "password$12"})
    public void should_returnBadRequest_whenInvalidPassword(String value)
            throws Exception {
        given_ValidUserDTO();
        userDTO.setPassword(value);
        log.debug("ParameterizedTest registerUser with invalid password" +
                " value={}", value);

        should_returnBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "email", "email.com", "email@", "@email", "@email.com"})
    public void should_returnBadRequest_whenInvalidEmail(String value)
            throws Exception {
        given_ValidUserDTO();
        userDTO.setEmail(value);
        log.debug("ParameterizedTest registerUser with invalid email" +
                " value={}", value);

        should_returnBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "Name12", "1212", "Name$"})
    public void should_returnBadRequest_whenInvalidFirstName(String value)
            throws Exception {
        given_ValidUserDTO();
        userDTO.setFirstName(value);
        log.debug("ParameterizedTest registerUser with invalid firstName" +
                " value={}", value);

        should_returnBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "Name12", "1212", "Name$"})
    public void should_returnBadRequest_whenInvalidLastName(String value)
            throws Exception {
        given_ValidUserDTO();
        userDTO.setLastName(value);
        log.debug("ParameterizedTest registerUser with invalid lastName" +
                " value={}", value);

        should_returnBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "(044)123321", "123321asd", "123-123", "099999999999999999999", "1"})
    public void should_returnBadRequest_whenInvalidPhoneNumber(String value)
            throws Exception {
        given_ValidUserDTO();
        userDTO.setPhoneNumber(value);
        log.debug("ParameterizedTest registerUser with invalid phoneNumber" +
                " value={}", value);

        should_returnBadRequest();
    }


    @Test
    public void should_returnUpdatedUser_whenUpdateUser() throws Exception {
        given_ValidUserDTO();

        /* TODO: Changes email due to email must be unique and default one is already
            used by another test. Probably should truncate DB before each test
            or delete freshly added row, but now I don't have required endpoint implemented
            and I guess I shouldn't use repository for this
         */
        userDTO.setEmail("emaill@gmail.com");
        String newUserJson = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andReturn().getResponse().getContentAsString();
        UserDTO newUserDTO = mapper.readValue(newUserJson, UserDTO.class);
        userDTO.setFirstName("newname");

        mockMvc.perform(
                put("/users/{id}", newUserDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(jsonPath("$.firstName")
                        .value(userDTO.getFirstName()));
    }
}
