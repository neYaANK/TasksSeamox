/*
 * UserAddressTest.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class UserAddressTest extends AbstractUserTest {
    @Autowired
    private UserAddressMapper addressMapper;
    @Autowired
    private UserAddressRepository addressRepository;
    private UserAddressDTO addressDTO;
    private int createdUserId = 1;
    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setPassword("Password$12");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@email.com");
        user.setPhoneNumber("+4343434343");
        user.setBirthDate(LocalDate.of(2010,10,10));
        user.setVerified(false);
        user = userRepository.save(user);
        createdUserId = user.getId();
    }
    @AfterEach
    public void tearDown(){
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    public void should_returnNewAddress_when_createValidAddress() throws Exception {
        given_validUserAddressDTO();
        log.debug("Test createValidAddress address = {}", addressDTO);

        mockMvc.perform(
                        post("/users/{id}/addresses",createdUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(addressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());
    }
    @Test
    public void should_returnNotFound_when_createAddressWithNonExistentUser() throws Exception {
        given_validUserAddressDTO();
        log.debug("Test createValidAddress when User is nonexistent address = {}", addressDTO);

        mockMvc.perform(
                        post("/users/{id}/addresses",createdUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(addressDTO)))
                .andExpect(status().isNotFound());
    }
    @Test
    public void should_updateAddress_when_updateValidAddress() throws Exception {
        given_validUserAddressDTO();
        log.debug("Test updateAddress address = {}", addressDTO);
        UserAddress address = addressMapper.toAddress(addressDTO);
        address.setUser(userRepository.getReferenceById(createdUserId));
        addressDTO.setCity("CHANGED");

        UserAddress newAddress = addressRepository.save(address);

        mockMvc.perform(
                        put("/users/{id}/addresses/{addressId}",
                                createdUserId, newAddress.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(addressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("CHANGED"));
    }
    @Test
    public void should_returnNotFound_when_updateAddressWithNonExistentUser() throws Exception {
        given_validUserAddressDTO();
        int nonExistentId = createdUserId+100;
        log.debug("Test updateAddress with nonexistent userId = {} ", nonExistentId);

        mockMvc.perform(
                        put("/users/{id}/addresses/{addressId}",
                                nonExistentId, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(addressDTO)))
                .andExpect(status().isNotFound());
    }
    //@Test
    //public void should_returnNotFound_when_updateAddressBelongToOtherUser() throws Exception {}

    @Test
    public void should_returnAllAddresses_when_getAllAddresses() throws Exception {

    }

    @Test
    public void should_returnNotFound_when_getAllAddressesWithNonExistentUser() throws Exception {

    }

    @Test
    public void should_returnSuccess_when_deleteExistentUser() throws Exception {

    }
    @Test
    public void should_returnNotFound_when_deleteNonExistentUser() throws Exception {

    }
    @Test
    public void should_returnNotFound_when_deleteNonExistentAddress() throws Exception {

    }
//    @Test
//    public void should_returnNotFound_when_deleteAddressFromAnotherUser() throws Exception{
//
//    }

    @Test
    public void should_returnSameDTO_when_userAddressMapper(){
        given_validUserAddressDTO();
        log.info(addressDTO.toString());
        UserAddress address = addressMapper.toAddress(addressDTO);
        log.info(address.toString());
        assertEquals(addressDTO.getId(), address.getId());
        assertEquals(addressDTO.getCountry(), address.getCountry());
        assertEquals(addressDTO.getCity(), address.getCity());
        assertEquals(addressDTO.getAddress(), address.getAddress());
        assertEquals(addressDTO.getZip(), address.getZip());
        assertEquals(addressDTO.getDetails(), address.getDetails());
    }

    public void given_validUserAddressDTO(){
        addressDTO = new UserAddressDTO(null,"AUT", "10000",
                "Vienna", "Strasse 1", "abcd", false);
    }
}
