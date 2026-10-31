package com.service.balanceService.service.kafka.kafkaListenerService;

import com.service.balanceService.dto.InventoryReserveResponseEvent;
import com.service.balanceService.dto.PaymentResponseEvent;
import com.service.balanceService.service.impl.BalanceServiceImpl;
import com.service.balanceService.service.kafka.kafkaProducerService.PaymentFailedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryResponseEventServiceListener {
    private final PaymentFailedEventService failedEventService;
    private final BalanceServiceImpl balanceService;

    @KafkaListener(topics = "inventory.response.event", groupId = "inventory.response.event.group", containerFactory = "inventoryResponseKafkaListenerFactory")
    public void updateUserBalanceHandler(InventoryReserveResponseEvent inventoryReserveResponseEvent) {
        String userId = inventoryReserveResponseEvent.getUserId();
        if (userId == null || userId.isEmpty()) {
            failedEventService.publishTransactionFailedEvent(
                    new PaymentResponseEvent(
                            inventoryReserveResponseEvent.getOrderId(),
                            false,
                            "Invalid or missing userId in inventory reserve response"
                    )
            );
            return; // Skip processing to avoid exception
        }
        try {
            balanceService.transferFunds(inventoryReserveResponseEvent.getRestaurantId(),inventoryReserveResponseEvent.getUserId(), inventoryReserveResponseEvent.getTotalPrice());
            failedEventService.publishTransactionFailedEvent(
                    new PaymentResponseEvent(
                            inventoryReserveResponseEvent.getOrderId(),
                            false,
                            "Failed to update balance: "
                    )
            );
        } catch (RuntimeException e) {
            throw e; // Re-throw to let Kafka retry mechanism handle it
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}