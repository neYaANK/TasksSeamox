/*
 * UserServiceImpl.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.neyaank.task2.email.EmailService;
import org.neyaank.task2.errorhandling.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional(readOnly = false)
    public User registerUser(User user) {
        user.setId(null);
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User res = userRepository.save(user);
        String verification = UUID.randomUUID().toString();
        res.setVerificationCode(verification);
        res = userRepository.save(res);
        emailService.sendVerificationEmail(user.getEmail(), verification);
        return res;
    }
    @Override
    @Transactional(readOnly = false)
    public User updateUser(int id, User user) {
        user.setId(null);
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
    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
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

    @Override
    public boolean existsById(int id) {
        return userRepository.existsById(id);
    }
    @Transactional(readOnly = false)
    @Override
    public void verify(String code) {
        Optional<User> user = userRepository.findByVerificationCode(code);
        if(user.isEmpty()){
            throw new NotFoundException("Verification code not found");
        }
        User newUser = user.get();
        newUser.setVerificationCode(null);
        newUser.setVerified(true);
        newUser = userRepository.save(newUser);
    }
}
