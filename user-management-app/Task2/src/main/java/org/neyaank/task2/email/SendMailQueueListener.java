/*
 * SendMailQueueListener.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.AsyncMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import software.amazon.eventstream.Message;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendMailQueueListener {
    private final JavaMailSender mailSender;
    @Value("${neya.verification.sender}")
    private String from;

    @SqsListener("SendMail")
    public void receiveMessage(VerificationEmail message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(message.getRecipientEmail());
        email.setSubject("Email Verification");
        email.setText(message.getMessage());
        log.debug("Sending verification email to {}", message.getRecipientEmail());
        mailSender.send(email);
    }
}
