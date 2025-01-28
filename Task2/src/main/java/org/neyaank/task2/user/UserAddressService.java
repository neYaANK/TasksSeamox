/*
 * UserAddressService.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import java.util.List;

public interface UserAddressService {
    UserAddress getUserAddressByIdAndUserId(int id, int userId);
    UserAddress createUserAddress(int userId, UserAddress userAddress);
    UserAddress updateUserAddressOfUser(int id, int userId, UserAddress userAddress);
    void deleteUserAddressOfAUser(int id, int userId);
    List<UserAddress> getAddressesOfUser(int userId);
}
