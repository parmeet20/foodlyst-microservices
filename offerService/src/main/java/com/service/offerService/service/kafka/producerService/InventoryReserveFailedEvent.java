package com.service.offerService.service.kafka.producerService;

import com.service.offerService.dto.InventoryReserveResponseEvent;
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
public class InventoryReserveFailedEvent {
    private final KafkaTemplate<String, InventoryReserveResponseEvent> template;
    private static final String TOPIC = "inventory.response.event";
    public void revertPrevProcesses(InventoryReserveResponseEvent dto){
        try {
            log.info(dto.getUserId());
            Message<InventoryReserveResponseEvent> message = MessageBuilder
                    .withPayload(dto)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    // Add type header for better deserialization
                    .setHeader(KafkaHeaders.KEY, dto.getOrderId()+ "-" + System.currentTimeMillis())
                    .build();

            CompletableFuture<SendResult<String, InventoryReserveResponseEvent>> future = template.send(message);

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
