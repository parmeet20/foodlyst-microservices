package com.service.userService.services.kafka.kafkaProducerService;

import com.service.userService.dto.DeleteRestaurantEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class DeleteRestaurantServiceEvent {
    private static final String TOPIC = "delete-user-restaurant-topic";
    @Autowired
    private KafkaTemplate<String, DeleteRestaurantEventDto> kafkaTemplate;
    public void createBalanceAccountProducer(DeleteRestaurantEventDto dto) {
        CompletableFuture<SendResult<String, DeleteRestaurantEventDto>> future = kafkaTemplate.send(TOPIC, dto);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                // 🔴 Failure case
                System.err.println("❌ Failed to send userId to Kafka: " + dto.getUserId());
                exception.printStackTrace();
                // You can log it or integrate with monitoring/alerting systems here
            } else {
                // ✅ Success case
                System.out.println("✅ Kafka message sent successfully for delete restaurant event: " + dto.getUserId() +
                        ", to partition: " + result.getRecordMetadata().partition() +
                        ", offset: " + result.getRecordMetadata().offset());
            }
        });
    }
}
