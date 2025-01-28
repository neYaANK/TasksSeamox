/*
 * UserAddressController.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users/{userId}/addresses")
@RequiredArgsConstructor
public class UserAddressController {
    private final UserAddressService addressService;
    private final UserAddressMapper addressMapper;
    @PostMapping
    public ResponseEntity create(@PathVariable(name = "userId") int userId,
                                 @Valid @RequestBody UserAddressDTO userAddress) {
        UserAddress address = addressMapper.toAddress(userAddress);
        UserAddress created = addressService.createUserAddress(userId,address);
        UserAddressDTO responseAddress = addressMapper.toDTO(created);
        return ResponseEntity.ok(responseAddress);
    }
    @GetMapping
    public ResponseEntity read(@PathVariable(name = "userId") int userId) {
        List<UserAddress> userAddressList = addressService.getAddressesOfUser(userId);
        List<UserAddressDTO> response = userAddressList.stream()
                .map(address -> addressMapper.toDTO(address)).toList();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{addressId}")
    public ResponseEntity update(@PathVariable(name = "userId") int userId,
                                 @PathVariable(name = "addressId") int addressId,
                                 @Valid @RequestBody UserAddressDTO userAddress) {
        UserAddress address = addressMapper.toAddress(userAddress);
        address = addressService.updateUserAddressOfUser(addressId, userId, address);
        UserAddressDTO responseAddress = addressMapper.toDTO(address);
        return ResponseEntity.ok(responseAddress);
    }
    @DeleteMapping("/{addressId}")
    public ResponseEntity delete(@PathVariable(name = "userId") int userId,
                                 @PathVariable(name = "addressId") int addressId) {
        addressService.deleteUserAddressOfAUser(addressId, userId);
        return ResponseEntity.ok().build();
    }


}
