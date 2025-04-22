/*
 * EmailServiceImpl.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SqsAsyncClient sqsClient;
    private final SqsTemplate sqsTemplate;
    @Value("${neya.public.url}")
    private String publicUrl;


    public void sendVerificationEmail(String to, String verificationCode, int userId) {
        String message = "Verify your email by clicking at this link: " +
                publicUrl + "/verification?code=" + verificationCode;
        VerificationEmail mail = new VerificationEmail(to, userId, message);
        var res = sqsTemplate.send("SendMail",mail);
    }

}
