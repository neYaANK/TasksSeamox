/*
 * UserServiceImpl.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.neyaank.task2.errorhandling.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

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

    @Override
    public User findUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new NotFoundException("User not found");
        }
        User toReturn = user.get();
        toReturn.setPassword("");
        return toReturn;
    }

    @Override
    public User getProxyById(int id) {
        User proxy = null;
        try {
            proxy = userRepository.getReferenceById(id);
        }catch (EntityNotFoundException e){
            throw new NotFoundException("User with id " + id + " not found");
        }
            return proxy;
    }
}
