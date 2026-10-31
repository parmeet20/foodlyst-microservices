package com.service.userService.services.kafka.kafkaProducerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class CreateCorrespondingBalanceAccount {

    private static final String TOPIC = "create-balance-account-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void createBalanceAccountProducer(String userId) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, userId);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                // 🔴 Failure case
                System.err.println("❌ Failed to send userId to Kafka: " + userId);
                exception.printStackTrace();
                // You can log it or integrate with monitoring/alerting systems here
            } else {
                // ✅ Success case
                System.out.println("✅ Kafka message sent successfully for userId: " + userId +
                        ", to partition: " + result.getRecordMetadata().partition() +
                        ", offset: " + result.getRecordMetadata().offset());
            }
        });
    }
}
