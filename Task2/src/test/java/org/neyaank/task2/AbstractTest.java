/*
 * AbstractTest.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.neyaank.task2.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.HandlerMapping;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class AbstractTest {
    @RegisterExtension
    protected static GreenMailExtension greenMail =
            new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("user", "admin"))
            .withPerMethodLifecycle(false);
    protected static SQSRestServer server;
    protected String queueUrl;

    @Autowired
    protected SqsClient sqsClient;
    @Autowired
    protected static JavaMailSender mailSender;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper mapper;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected UserAddressMapper addressMapper;
    @Autowired
    protected UserAddressRepository addressRepository;
    protected UserDTO userDTO;
    protected UserAddressDTO addressDTO;

    public void given_validUserDTO(){
        userDTO = new UserDTO(-1,"Pass1234$12","email@gmail.com",
                "fname", "lname",
                LocalDate.now().minusYears(13),
                "+430000000000", false);
    }

    public void given_validUserAddressDTO(){
        addressDTO = new UserAddressDTO(null,"AUT", "10000",
                "Vienna", "Strasse 1", "abcd", false);
    }

    @BeforeAll
    static void setUp() {
        server = SQSRestServerBuilder.withPort(9324).start();
        //One-time use client to set up default queue
        SqsClient client = SqsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .endpointOverride(URI.create("http://localhost:9324"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("x","x")))
                .build();
        CreateQueueRequest req = CreateQueueRequest.builder()
                .queueName("SendMail")
                .build();
        client.createQueue(req);
        client.close();
    }
    @AfterAll
    static void tearDown(){
        server.stopAndWait();
    }
}
