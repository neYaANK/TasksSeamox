/*
 * UserServiceImpl.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = false)
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    @Override
    @Transactional(readOnly = false)
    public User updateUser(int id, User user) {
        User toUpdate = userRepository.getReferenceById(id);
        toUpdate.copy(user);
        toUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(toUpdate);
    }
}
