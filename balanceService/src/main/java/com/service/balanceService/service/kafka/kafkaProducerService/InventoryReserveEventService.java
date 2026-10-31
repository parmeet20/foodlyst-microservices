package com.service.balanceService.service.kafka.kafkaProducerService;

import com.service.balanceService.dto.InventoryReserveRequestEvent;
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
@RequiredArgsConstructor
@Slf4j
public class InventoryReserveEventService {
    private final KafkaTemplate<String, InventoryReserveRequestEvent> template;
    private final static String TOPIC = "inventory.reserve.event";
    public void reserveInventory(InventoryReserveRequestEvent dto){
        try {
            Message<InventoryReserveRequestEvent> message = MessageBuilder
                    .withPayload(dto)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    // Add type header for better deserialization
                    .setHeader(KafkaHeaders.KEY, dto.getOrderId() + "-" + System.currentTimeMillis())
                    .build();

            CompletableFuture<SendResult<String, InventoryReserveRequestEvent>> future = template.send(message);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Error sending InventoryReserveRequestEvent: {}", ex.getMessage(), ex);
                } else {
                    log.info("Message InventoryReserveRequestEvent successfully published: {}", result.getProducerRecord().value());
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
