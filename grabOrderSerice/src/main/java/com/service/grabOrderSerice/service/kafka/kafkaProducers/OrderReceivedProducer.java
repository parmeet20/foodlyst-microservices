package com.service.grabOrderSerice.service.kafka.kafkaProducers;

import com.service.grabOrderSerice.dto.NotificationDto;
import com.service.grabOrderSerice.dto.Order;
import com.service.grabOrderSerice.dto.PaymentResponseEvent;
import com.service.grabOrderSerice.dto.UserResponseDto;
import com.service.grabOrderSerice.service.feignClients.OrderClient;
import com.service.grabOrderSerice.service.feignClients.UserClient;
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
public class OrderReceivedProducer {
    private final KafkaTemplate<String, PaymentResponseEvent> template;
    private final static String TOPIC = "payment.response.event";
    private final UserClient userClient;
    private final OrderClient orderClient;
    private final SendNotificationService notificationService;

    public void publishTransactionFailedEvent(PaymentResponseEvent dto) {
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
                    log.info("Message sent to order service successfully: {}", result.getProducerRecord().value());
                    log.info("Message sent to partition: {}, offset: {}",
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
            Order o = orderClient.getOrderById(dto.getOrderId()).getBody();
            UserResponseDto userResponseDto = userClient.getUserByIdHandler(Objects.requireNonNull(o).getUserId());
            String msg = String.format("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <style>
                        body {
                          font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                          background: #f9fafc;
                          padding: 40px;
                          text-align: center;
                          color: #333;
                        }
                        .card {
                          display: inline-block;
                          background: #fff;
                          border-radius: 12px;
                          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                          padding: 24px 36px;
                          max-width: 400px;
                        }
                        h2 {
                          color: #2a9d8f;
                          margin-bottom: 10px;
                        }
                        p {
                          font-size: 16px;
                          margin: 10px 0;
                          color: #555;
                        }
                        .highlight {
                          color: #264653;
                          font-weight: bold;
                        }
                        .emoji {
                          font-size: 28px;
                        }
                      </style>
                    </head>
                    <body>
                      <div class="card">
                        <div class="emoji">✅🎉</div>
                        <h2>Order Grabbed Successfully!</h2>
                        <p>Your order with ID <span class="highlight">#%s</span> has been successfully grabbed.</p>
                      </div>
                    </body>
                    </html>
                    """, o.getId());

            NotificationDto notificationDto = NotificationDto
                    .builder()
                    .recipient(userResponseDto.getEmail())
                    .createdAt(LocalDateTime.now())
                    .sentAt(LocalDateTime.now())
                    .message(msg)
                    .status("SUCCESS")
                    .type("GRABBED")
                    .build();
            notificationService.publishTransactionFailedEvent(notificationDto);
        } catch (Exception e) {
            log.error("Exception occurred while sending message to Kafka", e);
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }
}
