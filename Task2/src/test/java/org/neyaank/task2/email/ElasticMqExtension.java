/*
 * ElasticMqExtension.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.email;

import lombok.extern.slf4j.Slf4j;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

import java.net.URI;

/*
    Extension to initialize and shutdown ElasticMQ server only once during testing
*/
@Slf4j
public class ElasticMqExtension implements BeforeAllCallback, AfterAllCallback {
    private static boolean started = false;
    private static SQSRestServer server;


    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!started){
            int port = 9324;
            server = SQSRestServerBuilder.withPort(port).start();

            //One-time use client to set up default queue
            log.info("ElasticMQ Server started at port {}", port);
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
    }
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        server.stopAndWait();
    }
}
