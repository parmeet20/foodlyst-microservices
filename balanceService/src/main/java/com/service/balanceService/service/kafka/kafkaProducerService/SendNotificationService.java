package com.service.balanceService.service.kafka.kafkaProducerService;

import com.service.balanceService.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendNotificationService {
    private final KafkaTemplate<String, NotificationDto> template;
    private final static String TOPIC = "notification.event";

    public void publishTransactionFailedEvent(NotificationDto dto) {
        try {
            Message<NotificationDto> message = MessageBuilder
                    .withPayload(dto)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    // Add type header for better deserialization
                    .setHeader(KafkaHeaders.KEY, dto.getSentAt() + "-" + System.currentTimeMillis())
                    .build();

            CompletableFuture<SendResult<String, NotificationDto>> future = template.send(message);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Error sending message to notification service: {}", ex.getMessage(), ex);
                } else {
                    log.info("Message sent to notification service successfully: {}", result.getProducerRecord().value());
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
