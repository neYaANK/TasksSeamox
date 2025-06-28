/*
 * AddressTest.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.neyaank.task2.AbstractTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class UserAddressTest extends AbstractTest {

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
                        post("/users/{id}/addresses",-1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(addressDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_createPrimaryAddress_when_createAddressWithoutAddresses() throws Exception{
        given_validUserAddressDTO();
        log.debug("Test createValidAddress if address is primary when no addresses provided");

        mockMvc.perform(
                        post("/users/{id}/addresses",createdUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(addressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primary").value(true));
    }

    @Test
    public void should_replacePrimary_when_createAddressWithExistingPrimary() throws Exception{
        given_validUserAddressDTO();
        addressDTO.setPrimary(true);
        UserAddress address = addressMapper.toAddress(addressDTO);
        address.setUser(userRepository.getReferenceById(createdUserId));
        addressDTO.setCity("AddressThatIsPrimary");

        address = addressRepository.save(address);
        log.debug("Test createValidAddress replace primary when primary exists");


        mockMvc.perform(
                        post("/users/{id}/addresses",createdUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(addressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primary").value(true));
        assertEquals(addressRepository.findById(address.getId()).get().isPrimary(),
                false);
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
        int nonExistentId = -1;
        log.debug("Test updateAddress with nonexistent userId = {} ", nonExistentId);

        mockMvc.perform(
                        put("/users/{id}/addresses/{addressId}",
                                nonExistentId, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(addressDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_returnAllAddresses_when_getAllAddresses() throws Exception {
        given_validUserAddressDTO();
        UserAddress address = addressMapper.toAddress(addressDTO);
        address.setUser(userRepository.getReferenceById(createdUserId));
        addressRepository.save(address);
        addressRepository.save(address);

        mockMvc.perform(
                        get("/users/{id}/addresses",
                                createdUserId))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFound_when_getAllAddressesWithNonExistentUser() throws Exception {
        mockMvc.perform(
                        get("/users/{id}/addresses",
                                -1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_returnSuccess_when_deleteValidAddress() throws Exception {
        given_validUserAddressDTO();
        UserAddress address = addressMapper.toAddress(addressDTO);
        address.setUser(userRepository.getReferenceById(createdUserId));
        address = addressRepository.save(address);

        mockMvc.perform(
                        delete("/users/{id}/addresses/{addressId}",
                                createdUserId, address.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFound_when_deleteNonExistentUser() throws Exception {
        given_validUserAddressDTO();
        UserAddress address = addressMapper.toAddress(addressDTO);
        address.setUser(userRepository.getReferenceById(createdUserId));
        address = addressRepository.save(address);

        mockMvc.perform(
                        delete("/users/{id}/addresses/{addressId}",
                                -1, address.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFound_when_deleteNonExistentAddress() throws Exception {
        given_validUserAddressDTO();
        mockMvc.perform(
                        delete("/users/{id}/addresses/{addressId}",
                                createdUserId, -1))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings =
            {"", "AUSTRIA", "aus", "au5"})
    public void should_returnBadRequest_whenInvalidCountry(String value)
            throws Exception {
        given_validUserAddressDTO();
        addressDTO.setCountry(value);
        log.debug("ParameterizedTest createValidAddress with invalid country = {}", value);
        mockMvc.perform(
                        post("/users/{id}/addresses",createdUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(addressDTO)))
                .andExpect(status().isBadRequest());
    }

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
}
