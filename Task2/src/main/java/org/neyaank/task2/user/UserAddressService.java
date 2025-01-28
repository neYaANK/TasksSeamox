/*
 * UserAddressService.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import java.util.List;

public interface UserAddressService {
    UserAddress getUserAddressById(int id);
    UserAddress createUserAddress(int userId, UserAddress userAddress);
    UserAddress updateUserAddress(int id, UserAddress userAddress);
    void deleteUserAddress(int id);
    List<UserAddress> getAddressesOfUser(int userId);
}
