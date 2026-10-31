package com.service.transactionService.service.kafka.producerService;

import com.service.transactionService.dto.NotificationDto;
import com.service.transactionService.dto.Order;
import com.service.transactionService.dto.PaymentResponseEvent;
import com.service.transactionService.dto.UserResponseDto;
import com.service.transactionService.service.feignClients.OrderClient;
import com.service.transactionService.service.feignClients.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionSuccessProducerService {
    private final static String TOPIC = "transaction.response.event";
    private final KafkaTemplate<String, PaymentResponseEvent> template;
    private final SendNotificationService notificationService;
    private final UserClient userClient;
    private final OrderClient orderClient;

    public void txSuccessProducer(PaymentResponseEvent dto) {
        try {
            Message<PaymentResponseEvent> message = MessageBuilder
                    .withPayload(dto)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    // Add type header for better deserialization
                    .setHeader(KafkaHeaders.KEY, dto.getOrderId() + "-" + System.currentTimeMillis())
                    .build();

            CompletableFuture<SendResult<String, PaymentResponseEvent>> future = template.send(message);

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
