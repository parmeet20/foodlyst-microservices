package com.service.balanceService.service.kafka.kafkaProducerService;

import com.service.balanceService.dto.CreateTransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PublishPaymentToTransaction {

    private static final String TOPIC = "payment-transaction-topic";

    // Use Object instead of specific DTO to avoid type issues, or configure proper type mapping
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void createTransactionEntry(CreateTransactionDto dto) {
        try {
            Message<CreateTransactionDto> message = MessageBuilder
                    .withPayload(dto)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    // Add type header for better deserialization
                    .setHeader(KafkaHeaders.KEY, dto.getSourceAccount() + "-" + System.currentTimeMillis())
                    .build();

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Error sending message to payment service: {}", ex.getMessage(), ex);
                } else {
                    log.info("Message sent to payment service successfully: {}", result.getProducerRecord().value());
                    log.info("Message sent to partition: {}, offset: {}",
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });

        } catch (Exception e) {
            log.error("Exception occurred while sending message to Kafka", e);
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }
}