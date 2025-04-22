/*
 * Test.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import com.icegreen.greenmail.store.FolderException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.neyaank.task2.AbstractTest;
import org.neyaank.task2.email.ElasticMqExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith({ElasticMqExtension.class})
public class UserTest extends AbstractTest {
    @Value("${neya.scheduler.delay}")
    private int schedulerRate;

    @AfterEach
    public void tearDown() throws FolderException {
        userRepository.deleteAll();
        userRepository.flush();
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    public void should_returnNewUser_when_registerValidUser() throws Exception {
        given_validUserDTO();
        log.info("Test registerValidUser user = {}", userDTO);
        registerUser(userDTO)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "Pass$1", "passworddd", "PASSDWORDDD", "$$$$$$$$$$", "Password12",
                    "Password$$", "password$12"})
    public void should_returnBadRequest_when_invalidPassword(String value)
            throws Exception {
        given_validUserDTO();
        userDTO.setPassword(value);
        log.debug("ParameterizedTest registerUser with invalid password" +
                " value={}", value);

        registerUser(userDTO).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "email", "email.com", "email@", "@email", "@email.com"})
    public void should_returnBadRequest_when_invalidEmail(String value)
            throws Exception {
        given_validUserDTO();
        userDTO.setEmail(value);
        log.debug("ParameterizedTest registerUser with invalid email" +
                " value={}", value);

        registerUser(userDTO).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "Name12", "1212", "Name$"})
    public void should_returnBadRequest_when_invalidFirstName(String value)
            throws Exception {
        given_validUserDTO();
        userDTO.setFirstName(value);
        log.debug("ParameterizedTest registerUser with invalid firstName" +
                " value={}", value);

        registerUser(userDTO).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "Name12", "1212", "Name$"})
    public void should_returnBadRequest_when_invalidLastName(String value)
            throws Exception {
        given_validUserDTO();
        userDTO.setLastName(value);
        log.debug("ParameterizedTest registerUser with invalid lastName" +
                " value={}", value);

        registerUser(userDTO).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "(044)123321", "123321asd", "123-123", "099999999999999999999", "1"})
    public void should_returnBadRequest_when_invalidPhoneNumber(String value)
            throws Exception {
        given_validUserDTO();
        userDTO.setPhoneNumber(value);
        log.debug("ParameterizedTest registerUser with invalid phoneNumber" +
                " value={}", value);

        registerUser(userDTO).andExpect(status().isBadRequest());
    }

    //Parameterized test in case we will need to do more testing for birthDate validation
    @ParameterizedTest
    @CsvSource({"2999-01-01"})
    public void should_returnBadRequest_when_invalidBirthDate(LocalDate value)
            throws Exception {
        given_validUserDTO();
        userDTO.setBirthDate(value);
        log.debug("ParameterizedTest registerUser with invalid birthDate" +
                " value={}", value);

        registerUser(userDTO).andExpect(status().isBadRequest());
    }

    @Test
    public void should_sendVerificationEmail_when_registerUser() throws Exception{
        given_validUserDTO();
        log.info("Test registerValidUser should receive Email");

        registerUser(userDTO);
        await()
                .atMost(10, SECONDS)
                .until(()-> greenMail.getReceivedMessages().length == 1);
    }

    @Test
    public void should_returnUpdatedUser_when_updateUser() throws Exception {
        given_validUserDTO();
        User newUser = userMapper.userDTOToUser(userDTO);
        newUser.setId(null);

        newUser = userRepository.save(newUser);
        UserDTO newUserDTO = userMapper.userToUserDTO(newUser);
        log.debug("Test updateUser newUser = {}", userDTO);
        userDTO.setFirstName("newname");

        updateUser(newUserDTO.getId(), userDTO)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName")
                        .value(userDTO.getFirstName()));
    }

    @Test
    public void should_returnUnverifiedUser_when_updateEmail() throws Exception {
        given_validUserDTO();
        String newEmail = "new.email@test.com";
        userDTO.setEmail(newEmail);
        User newUser = userMapper.userDTOToUser(userDTO);
        newUser.setId(null);
        newUser = userRepository.save(newUser);
        UserDTO newUserDTO = userMapper.userToUserDTO(newUser);
        log.debug("Test updateUser with new Email = {}", userDTO);

        updateUser(newUserDTO.getId(), userDTO)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value(newEmail))
                .andExpect(jsonPath("$.verified")
                        .value(false));
    }

    @Test
    public void should_sendEmail_when_updateEmail() throws Exception {
        given_validUserDTO();
        String newEmail = "new.email@test.com";
        User newUser = userMapper.userDTOToUser(userDTO);
        userDTO.setEmail(newEmail);
        newUser.setId(null);

        newUser = userRepository.save(newUser);
        UserDTO newUserDTO = userMapper.userToUserDTO(newUser);
        log.debug("Test updateUser sendEmail = {}", userDTO);

        updateUser(newUserDTO.getId(), userDTO);
        await()
                .atMost(10, SECONDS)
                .until(()-> greenMail.getReceivedMessages().length == 1);
    }

    @Test
    public void should_sendNoEmail_when_updateSameEmail() throws Exception {
        given_validUserDTO();
        User newUser = userMapper.userDTOToUser(userDTO);
        userDTO.setLastName("newLastName");
        newUser.setId(null);

        newUser = userRepository.save(newUser);
        UserDTO newUserDTO = userMapper.userToUserDTO(newUser);
        log.debug("Test updateUser sendNoEmail = {}", userDTO);

        updateUser(newUserDTO.getId(), userDTO);
        await()
                .atMost(10, SECONDS)
                .pollDelay(1800, MILLISECONDS)
                .until(()-> greenMail.getReceivedMessages().length == 0);
    }

    @Test
    public void should_returnUser_when_uetUserById() throws Exception {
        given_validUserDTO();
        User user = userMapper.userDTOToUser(userDTO);
        user.setId(null);

        User newUser = userRepository.save(user);
        log.debug("Test getUser savedUser = {}", newUser);

       getUser(newUser.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(newUser.getId()))
                .andExpect(jsonPath("$.email")
                        .value(user.getEmail()));
    }

    @Test
    public void should_returnNoPassword_when_getUserById() throws Exception {
        given_validUserDTO();
        User user = userMapper.userDTOToUser(userDTO);
        user.setId(null);

        User newUser = userRepository.save(user);
        log.debug("Test getUser returnNoPassword newUser = {}", userDTO);

        getUser(newUser.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password")
                        .isEmpty());
    }

    @Test
    public void should_return404_when_getNonExistentUser() throws Exception {
       getUser(1)
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_mapUserCorrectly_when_mapUserDtoToUser(){
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

    @Test
    public void should_deleteUser_when_scheduledDeletion() {
        given_validUserDTO();
        userDTO.setId(null);
        User user1 = userMapper.userDTOToUser(userDTO);
        user1.setVerificationStartTime(LocalDateTime.now().minusHours(30));
        User user2 = userMapper.userDTOToUser(userDTO);
        user2.setVerificationStartTime(LocalDateTime.now().minusHours(30));
        user2.setEmail("email2@email.com");
        log.debug("Test scheduleDeletion when user is old enough to be deleted {}",
                user1);

        userRepository.save(user1);
        userRepository.save(user2);

        await()
                .atMost(schedulerRate*2, SECONDS)
                .pollInterval(1, SECONDS)
                .pollDelay(schedulerRate, SECONDS)
                .until(()-> userRepository.findAll().isEmpty());
    }

    @Test
    public void should_notDeleteUser_when_scheduledDeletion() {
        given_validUserDTO();
        userDTO.setId(null);
        User user1 = userMapper.userDTOToUser(userDTO);
        user1.setVerificationStartTime(LocalDateTime.now().minusHours(23));
        log.debug("Test scheduleDeletion when user is not old enough to be deleted {}",
                user1);

        userRepository.save(user1);

        await()
                .atMost(schedulerRate*2, SECONDS)
                .pollInterval(1, SECONDS)
                .pollDelay(schedulerRate, SECONDS)
                .until(()->userRepository.findAll().size() == 1);
    }

    @Test
    public void should_returnOneUserPage_when_getSecondPage() throws Exception {
        given_validUserDTO();
        userDTO.setId(null);
        User user1 = userMapper.userDTOToUser(userDTO);
        user1.setVerificationStartTime(LocalDateTime.now());
        User user2 = userMapper.userDTOToUser(userDTO);
        user2.setVerificationStartTime(LocalDateTime.now());
        user2.setEmail("email2@email.com");
        log.debug("Test getAll when second page has 1 user {}", user2);

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        getUsers(2,1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(user2.getId()));
    }

    @Test
    public void should_returnBadRequest_when_getNonPositivePage() throws Exception {
        given_validUserDTO();
        userDTO.setId(null);
        User user1 = userMapper.userDTOToUser(userDTO);
        user1.setVerificationStartTime(LocalDateTime.now());
        log.debug("Test getAll when non positive page");

        user1 = userRepository.save(user1);

        getUsers(0,1)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_returnBadRequest_when_getNonPositivePageSize() throws Exception {
        given_validUserDTO();
        userDTO.setId(null);
        User user1 = userMapper.userDTOToUser(userDTO);
        user1.setVerificationStartTime(LocalDateTime.now());
        log.debug("Test getAll when non positive pageSize");

        user1 = userRepository.save(user1);

        getUsers(1,0)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_returnNoUserPage_when_getSecondPage() throws Exception {
        given_validUserDTO();
        userDTO.setId(null);
        User user1 = userMapper.userDTOToUser(userDTO);
        user1.setVerificationStartTime(LocalDateTime.now());
        log.debug("Test getAll when second page has no user");

        user1 = userRepository.save(user1);

        getUsers(2,1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").doesNotExist());
    }

    public ResultActions registerUser(UserDTO userDTO) throws Exception {
        return mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)));
    }

    public ResultActions updateUser(int id, UserDTO userDTO) throws Exception {
        return mockMvc.perform(
                put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)));
    }

    public ResultActions getUser(int id) throws Exception {
        return mockMvc.perform(
                get("/users/{id}", id));
    }
    public ResultActions getUsers(int page, int pageSize) throws Exception {
        return mockMvc.perform(
                get("/users")
                        .param("page", String.valueOf(page))
                        .param("pageSize", String.valueOf(pageSize)));
    }
}
