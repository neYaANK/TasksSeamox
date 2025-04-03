/*
 * EmailServiceImpl.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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
    private final SqsClient sqsClient;
    @Value("${neya.public.url}")
    private String publicUrl;
    @Value("${spring.mail.username}")
    private String from;
    @Value("${neya.elasticmq.queue}")
    private String queue;

    public void sendVerificationEmail(String to, String verificationCode, int userId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Email Verification");
        message.setText("Verify your email by clicking at this link: " +
                publicUrl + "/verification?code=" + verificationCode);
        log.debug("Sending verification email to {}", to);
        mailSender.send(message);
        sendMessageToQueue(queue, to, userId, message.getText());
    }

    private void sendMessageToQueue(String name, String receiver,
                                    int userId, String message) {
            GetQueueUrlRequest req = GetQueueUrlRequest.builder()
                    .queueName(name).build();
            String url = sqsClient.getQueueUrl(req).queueUrl();
            Map<String,MessageAttributeValue> args = new HashMap<>();
            args.put("destination", MessageAttributeValue.builder()
                    .stringValue(receiver)
                    .dataType("String")
                    .build());
            args.put("userid", MessageAttributeValue.builder()
                    .stringValue(String.valueOf(userId))
                    .dataType("Number")
                    .build());
            log.debug("Sending message to message queue {{};{}} {}",receiver, userId, url);
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(url)
                    .messageBody(message)
                    .messageAttributes(args)
                    .delaySeconds(5)
                    .build();
            sqsClient.sendMessage(sendMsgRequest);
    }
}
