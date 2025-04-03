/*
 * EmailConfig.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;


import java.net.URI;

@Configuration
public class EmailConfig {
    @Value("${neya.elasticmq.url}")
    private String elasticmq;

    @Bean
    public SqsClient sqsClient() {
        SqsClient client = SqsClient.builder()
                .httpClientBuilder(ApacheHttpClient.builder())
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials.create("X","X")))
                .endpointOverride(URI.create(elasticmq))
                .build();
        return client;
    }
}
