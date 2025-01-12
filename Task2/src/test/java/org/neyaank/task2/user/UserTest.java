/*
 * UserTest.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    private UserDTO userDTO;

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    public void should_returnNewUser_whenRegisterValidUser() throws Exception {
        given_validUserDTO();
        log.debug("Test registerValidUser user = {}", userDTO);

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "Pass$1", "passworddd", "PASSDWORDDD", "$$$$$$$$$$", "Password12",
                    "Password$$", "password$12"})
    public void then_returnBadRequest_whenInvalidPassword(String value)
            throws Exception {
        given_validUserDTO();
        userDTO.setPassword(value);
        log.debug("ParameterizedTest registerUser with invalid password" +
                " value={}", value);

        then_returnBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "email", "email.com", "email@", "@email", "@email.com"})
    public void then_returnBadRequest_whenInvalidEmail(String value)
            throws Exception {
        given_validUserDTO();
        userDTO.setEmail(value);
        log.debug("ParameterizedTest registerUser with invalid email" +
                " value={}", value);

        then_returnBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "Name12", "1212", "Name$"})
    public void then_returnBadRequest_whenInvalidFirstName(String value)
            throws Exception {
        given_validUserDTO();
        userDTO.setFirstName(value);
        log.debug("ParameterizedTest registerUser with invalid firstName" +
                " value={}", value);

        then_returnBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "Name12", "1212", "Name$"})
    public void then_returnBadRequest_whenInvalidLastName(String value)
            throws Exception {
        given_validUserDTO();
        userDTO.setLastName(value);
        log.debug("ParameterizedTest registerUser with invalid lastName" +
                " value={}", value);

        then_returnBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "(044)123321", "123321asd", "123-123", "099999999999999999999", "1"})
    public void then_returnBadRequest_whenInvalidPhoneNumber(String value)
            throws Exception {
        given_validUserDTO();
        userDTO.setPhoneNumber(value);
        log.debug("ParameterizedTest registerUser with invalid phoneNumber" +
                " value={}", value);

        then_returnBadRequest();
    }
    //Parameterized test in case we will need to do more testing for birthDate validation
    @ParameterizedTest
    @CsvSource({"2999-01-01"})
    public void then_returnBadRequest_whenInvalidBirthDate(LocalDate value)
            throws Exception {
        given_validUserDTO();
        userDTO.setBirthDate(value);
        log.debug("ParameterizedTest registerUser with invalid birthDate" +
                " value={}", value);

        then_returnBadRequest();
    }


    @Test
    public void should_returnUpdatedUser_whenUpdateUser() throws Exception {
        given_validUserDTO();
        User newUser = userMapper.userDTOToUser(userDTO);
        newUser.setId(null);

        //Use repository instead of request to decouple test from save implementation
        newUser = userRepository.save(newUser);
        UserDTO newUserDTO = userMapper.userToUserDTO(newUser);
        log.debug("Test updateUser newUser = {}", userDTO);
        userDTO.setFirstName("newname");

        mockMvc.perform(
                put("/users/{id}", newUserDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName")
                        .value(userDTO.getFirstName()));
    }
    @Test
    public void should_returnUser_whenGetUserById() throws Exception {
        given_validUserDTO();
        User user = userMapper.userDTOToUser(userDTO);
        user.setId(null);

        User newUser = userRepository.save(user);
        log.debug("Test getUser savedUser = {}", newUser);

        mockMvc.perform(
                        get("/users/{id}", newUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(newUser.getId()))
                .andExpect(jsonPath("$.email")
                        .value(user.getEmail()));
    }
    @Test
    public void should_returnNoPassword_whenGetUserById() throws Exception {
        given_validUserDTO();
        User user = userMapper.userDTOToUser(userDTO);
        user.setId(null);

        User newUser = userRepository.save(user);
        log.debug("Test getUser returnNoPassword newUser = {}", userDTO);

        mockMvc.perform(
                        get("/users/{id}", newUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password")
                        .isEmpty());
    }
    @Test
    public void should_return404_whenGetNonExistentUser() throws Exception {
        mockMvc.perform(
                        get("/users/{id}", 1))
                .andExpect(status().isNotFound());
    }
    @Test
    public void should_mapUserCorrectly_whenMapUserDtoToUser(){
        given_validUserDTO();

        User user = userMapper.userDTOToUser(userDTO);
        log.debug("Test userMapper: userDTO={}, user={}", userDTO, user);

        assertEquals(userDTO.getId(), user.getId());
        assertEquals(userDTO.getPassword(), user.getPassword());
        assertEquals(userDTO.getEmail(), user.getEmail());
        assertEquals(userDTO.getBirthDate(), user.getBirthDate());
        assertEquals(userDTO.getFirstName(), user.getFirstName());
        assertEquals(userDTO.getLastName(), user.getLastName());
        assertEquals(userDTO.getPhoneNumber(), user.getPhoneNumber());
    }

    //Make tests more readable
    public void given_validUserDTO(){
        userDTO = new UserDTO(-1,"Pass1234$12","email@gmail.com",
                "fname", "lname",
                LocalDate.now().minusYears(13),
                "+430000000000", false);
    }
    public void then_returnBadRequest() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }
}
