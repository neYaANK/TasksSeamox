/*
 * VerificationController.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import org.neyaank.task2.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verification")
public class VerificationController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @GetMapping
    public ResponseEntity verification(@RequestParam String code) {
        userService.verify(code);
        return ResponseEntity.ok().build();
    }
}
