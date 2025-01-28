/*
 * UserAddressServiceImpl.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neyaank.task2.errorhandling.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {
    private final UserAddressRepository addressRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserService userService;
    @Override
    public UserAddress getUserAddressByIdAndUserId(int userId, int id) {
        Optional<UserAddress> userAddress =
                addressRepository.findByUser_IdAndId(userId, id);
        if(userAddress.isEmpty()) {
            throw new NotFoundException("User address with id " + id + " not found");
        }
        log.debug("UserAddress by id {}: {}", id, userAddress);
        return userAddress.get();
    }

    @Override
    @Transactional(readOnly = false)
    public UserAddress createUserAddress(int userId, UserAddress userAddress) {
        if(!userService.existsById(userId)){
            throw new NotFoundException("User with id " + userId + " not found");
        }
        userAddress.setId(null);
        User user = userService.getProxyById(userId);
        userAddress.setUser(user);
        UserAddress address = addressRepository.save(userAddress);
        log.debug("UserAddress created {}", userAddress);
        return address;
    }

    @Override
    @Transactional(readOnly = true)
    public UserAddress updateUserAddressOfUser(int id, int userId, UserAddress userAddress) {
        Optional<UserAddress> oldAddress;
        if(!userService.existsById(userId)){
            throw new NotFoundException("User with id "+userId+" not found");
        }
        if((oldAddress = addressRepository.findByUser_IdAndId(userId, id))
                .isEmpty()) {
            throw new NotFoundException
                    ("User address with id " + id + " in user "+ userId +" not found");
        }

        userAddress.setId(id);
        //Ensure that owning User is not changed
        userAddress.setUser(oldAddress.get().getUser());
        UserAddress address = addressRepository.save(userAddress);
        log.debug("UserAddress with id {} updated, new = {}",id, userAddress);
        return address;
    }

    @Override
    @Transactional(readOnly = true)
    public void deleteUserAddressOfAUser(int userId, int id) {
        userAddressRepository.deleteByIdAndUserId(userId, id);
    }
    @Override
    public List<UserAddress> getAddressesOfUser(int userId) {
        User user = userService.findUserById(userId);
        return new ArrayList<>(user.getAddresses());
    }
}
