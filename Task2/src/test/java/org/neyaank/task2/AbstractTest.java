/*
 * AbstractTest.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.neyaank.task2.email.ElasticMqExtension;
import org.neyaank.task2.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(ElasticMqExtension.class)
public class AbstractTest {
    @RegisterExtension
    protected static GreenMailExtension greenMail =
            new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("user", "admin"))
            .withPerMethodLifecycle(false);

    @Autowired
    protected SqsAsyncClient sqsClient;
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
}
