/*
 * AbstractTest.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.neyaank.task2.email.ElasticMqExtension;
import org.neyaank.task2.email.SqsTestConfiguration;
import org.neyaank.task2.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.Duration;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ExtendWith(ElasticMqExtension.class)
@Import(SqsTestConfiguration.class)
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
    protected JavaMailSender mailSender;
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
    @Autowired
    protected S3Template s3;
    @Autowired
    protected UserService userService;
    protected UserDTO userDTO;
    protected UserAddressDTO addressDTO;
    /*

        According to the task, I should add S3Mock in docker-compose and
        use it to store user images.
        That's why I assume that I should use S3Mock in the main application.
        But remembering ElasticMQ (and that I should've used it in tests only),
        I looked up how to set up containers or run docker-compose in tests.
        I didn't find any proper way to use compose in tests except of running
        compose up separately, so I started it with the help of test containers.

     */
    @Container
    static GenericContainer<?> s3MockContainer = new GenericContainer<>(
            "adobe/s3mock:4.1.1")
            .withExposedPorts(9090)
            .withEnv("initialBuckets", "UserImages")
            .withStartupTimeout(Duration.ofSeconds(90));

    @DynamicPropertySource
    static void configureS3Properties(DynamicPropertyRegistry registry) {
        s3MockContainer.start();
        String endpoint = "http://" + s3MockContainer.getHost() + ":" + s3MockContainer.getMappedPort(9090);
        log.info("S3 Endpoint {}", endpoint);
        //spring
        registry.add("spring.cloud.aws.s3.endpoint", () -> endpoint);
        registry.add("spring.cloud.aws.s3.region", () -> "eu-central-1");
        registry.add("spring.cloud.aws.credentials.access-key", () -> "x");
        registry.add("spring.cloud.aws.credentials.secret-key", () -> "x");
    }

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
