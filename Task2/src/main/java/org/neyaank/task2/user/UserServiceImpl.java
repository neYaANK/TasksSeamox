/*
 * UserServiceImpl.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neyaank.task2.email.EmailService;
import org.neyaank.task2.errorhandling.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ImageService imageService;
    @Value("${neya.login.locktime}")
    private final int LOCK_MINUTES = 1;



    @Override
    @Transactional
    public User registerUser(User user) {
        user.setId(null);
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String verification = UUID.randomUUID().toString();
        user.setVerificationCode(verification);
        user.setVerificationStartTime(LocalDateTime.now());
        User res = userRepository.save(user);
        log.info("User registered {}", res);
        emailService.sendVerificationEmail(user.getEmail(),
                verification, res.getId());
        return res;
    }

    @Override
    @Transactional
    public User updateUser(int id, User user) {
        User oldUser = findUserById(id);
        user.setId(id);
        boolean emailChanged = false;
        //Request new verification if Email is changed
        if(!user.getEmail().equals(oldUser.getEmail())) {
            unverify(oldUser.getEmail());
            String verification = UUID.randomUUID().toString();
            user.setVerificationCode(verification);
            user.setVerificationStartTime(LocalDateTime.now());
            emailChanged = true;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        if(emailChanged) {
            emailService.sendVerificationEmail(user.getEmail(),
                    user.getVerificationCode(), user.getId());
        }
        log.info("Updating User {} with {}", user.getEmail(), user);
        return user;
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
        User user = findUserByEmailWithPassword(email);
        user.setPassword("");
        return user;
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

    @Transactional
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
        log.info("User {} is now verified", newUser.getEmail());
    }

    @Transactional
    @Override
    public void unverify(String email) {
        User user = findUserByEmail(email);
        user.setVerificationCode(null);
        user.setVerified(false);
        user = userRepository.save(user);
        log.info("User {} is now not verified", email);
    }

    // At first, I wanted to put these methods in AuthService, but I guess I should keep
    // all methods that utilize UserRepository in one place
    @Transactional
    @Override
    public User incrementFailedAttempts(String email) {
        User user = findUserByEmailWithPassword(email);
        user.incrementFailed();
        user = userRepository.save(user);
        log.info("Increasing failed login attempts for user {} to {}",
                user.getEmail(), user.getFailedAttempts());
        return user;
    }

    @Transactional
    @Override
    public User resetFailedAttempts(String email){
        User user = findUserByEmailWithPassword(email);
        user.resetFailed();
        user = userRepository.save(user);
        log.info("Resetting failed login attempts for user {}",
                user.getEmail());
        return user;
    }

    @Transactional
    @Override
    public User lockAccount(String email) {
        User user = findUserByEmailWithPassword(email);
        user.setLocked(true);
        user.setUnlockTime(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
        user = userRepository.save(user);
        log.info("Locking user {}", email);
        return user;
    }

    @Transactional
    @Override
    public User unlockAccount(String email){
        User user = findUserByEmailWithPassword(email);
        user.setLocked(false);
        user.setUnlockTime(null);
        user = userRepository.save(user);
        log.info("Unlocking user {}", email);
        return user;
    }

    @Override
    public int deleteUnverifiedOldUsers(int hoursAgeUntilDeletion) {
        int affected = userRepository.deleteUnverifiedAndOldEnough(
                LocalDateTime.now().minusHours(hoursAgeUntilDeletion));
        log.info("{} unverified users deleted", affected);
        return affected;
    }

    @Override
    public List<User> findAll(Pageable pageable) {
        List<User> res = userRepository.findAll(pageable).toList();
        log.debug("{} users at page {} with size {} found",
                res.size(), pageable.getPageNumber(), pageable.getPageSize());
        return res;
    }

    @Override
    public byte[] getImage(int id) throws IOException {
        byte[] img = imageService.getImage(String.valueOf(id));
        return img;
    }

    @Override
    public void uploadImage(int id, MultipartFile image) {
        try {
            imageService.saveImage(image.getInputStream(),
                    String.valueOf(id));
        } catch (IOException e) {
            throw new IllegalArgumentException("Image not supported");
        }
    }

    //Helper method for easier updating
    private User findUserByEmailWithPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new NotFoundException("User not found");
        }
        User toReturn = user.get();
        return toReturn;
    }

}
