/*
 * EmailServiceImpl.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender mailSender;
    @Value("${neya.public.url}")
    private String publicUrl;
    @Value("${spring.mail.username}")
    private String from;
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Email Verification");
        message.setText("Verify your email by clicking at this link: " +
                publicUrl+"/verification?code="+verificationCode);
        log.debug("Sending verification email to {}", to);
        mailSender.send(message);
    }
}
