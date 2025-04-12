/*
 * EmailConfig.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;


import java.net.URI;

@Configuration
@Slf4j
public class SqsConfig {

    @Profile("dev")
    @Bean
    public SqsAsyncClient sqsClient() {
        log.debug("Creating sqsClient dev instance");
        SqsAsyncClient client = SqsAsyncClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        return client;
    }

    @Profile("test")
    @Bean
    public SqsAsyncClient sqsElasticMqClient() {
        log.debug("Creating sqsClient test instance");
        SqsAsyncClient client = SqsAsyncClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create("X", "X")))
                .endpointOverride(URI.create("http://localhost:9324"))
                .build();
        return client;
    }
}
