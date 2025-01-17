/*
 * UserAddressServiceImpl.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neyaank.task2.errorhandling.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {
    private final UserAddressRepository addressRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserService userService;
    @Override
    public UserAddress getUserAddressById(int id) {
        Optional<UserAddress> userAddress = addressRepository.findById(id);
        if(userAddress.isEmpty()) {
            throw new NotFoundException("User address with id " + id + " not found");
        }
        return userAddress.get();
    }

    @Override
    public UserAddress createUserAddress(int userId, UserAddress userAddress) {
        userAddress.setId(null);
        User user = userService.getProxyById(userId);
        userAddress.setUser(user);
        UserAddress address = addressRepository.save(userAddress);
        log.debug("UserAddress created {}", userAddress);
        return address;
    }

    @Override
    public UserAddress updateUserAddress(int id, UserAddress userAddress) {
        if(!addressRepository.existsById(id)) {
            throw new NotFoundException("User address with id " + id + " not found");
        }

        userAddress.setId(id);
        UserAddress address = addressRepository.save(userAddress);
        return address;
    }

    @Override
    public void deleteUserAddress(int id) {
        userAddressRepository.deleteById(id);
    }

    @Override
    public List<UserAddress> getAddressesOfUser(int userId) {
        User user = userService.findUserById(userId);
        return new ArrayList<>(user.getAddresses());
    }
}
