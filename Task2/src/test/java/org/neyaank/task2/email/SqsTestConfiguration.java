/*
 * SqsTestConfiguration.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@TestConfiguration
@Slf4j
public class SqsTestConfiguration {
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
