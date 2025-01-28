/*
 * UserAddressRepository.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {
    void deleteByIdAndUserId(int userId, int addressId);
    Optional<UserAddress> findByUser_IdAndId(int userId, int addressId);
}
