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
    @Value("${neya.public.url}")
    private String publicUrl;
    @Value("${spring.mail.username}")
    private String from;
    @Value("${neya.elasticmq.queue}")
    private String queue;

    @SqsListener("SendMail")
    public void receiveMessage(SendMailPojo pojo) {
        log.info("SEND EMAIL");
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(pojo.getDestination());
        email.setSubject("Email Verification");
        email.setText("Verify your email by clicking at this link: " +
                publicUrl + "/verification?code=" +
                pojo.getVerificationCode());
        log.debug("Sending verification email to {}", pojo.getDestination());
        mailSender.send(email);
    }
}
