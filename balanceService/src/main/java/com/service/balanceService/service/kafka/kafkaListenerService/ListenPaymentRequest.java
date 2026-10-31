package com.service.balanceService.service.kafka.kafkaListenerService;

import com.service.balanceService.dto.InventoryReserveRequestEvent;
import com.service.balanceService.dto.PaymentRequestEvent;
import com.service.balanceService.dto.PaymentResponseEvent;
import com.service.balanceService.service.BalanceService;
import com.service.balanceService.service.kafka.kafkaProducerService.InventoryReserveEventService;
import com.service.balanceService.service.kafka.kafkaProducerService.PaymentFailedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * This class listens to incoming payment requests from the OrderService.
 * It validates balance availability, deducts funds, and triggers the next saga step.
 * If payment fails (insufficient balance / system error), it emits a PaymentFailed event.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ListenPaymentRequest {

    private final BalanceService balanceService;
    private final PaymentFailedEventService paymentFailedEventService;
    private final InventoryReserveEventService inventoryReserveEventService;

    @KafkaListener(topics = "payment.request.event", groupId = "payment.request.event.group",containerFactory = "paymentRequestKafkaListenerFactory")
    public void processPaymentEventListener(PaymentRequestEvent dto) {
        log.info("🎧 Received PaymentRequestEvent for Order ID: {}", dto.getOrderId());
        log.debug("Event payload: {}", dto);

        try {
            // Try to perform fund transfer
            boolean transferSuccess = balanceService.transferFunds(dto.getUserId(), dto.getRestaurantId(), dto.getTotalPrice());

            if (!transferSuccess) {
                // unlikely to reach here (since exception is thrown in case of insufficient balance)
                log.warn("⚠️ Payment transfer failed for Order ID: {}", dto.getOrderId());
                publishPaymentFailure(dto.getOrderId(), "Transfer unsuccessful");
                return;
            }

            // ✅ Payment successful — trigger inventory reservation
            log.info("💰 Payment successful for Order ID: {}. Proceeding to reserve inventory.", dto.getOrderId());
            InventoryReserveRequestEvent reserveRequestEvent = InventoryReserveRequestEvent.builder()
                    .orderId(dto.getOrderId())
                    .userId(dto.getUserId())
                    .foodOfferId(dto.getFoodOfferId())
                    .quantity(dto.getQuantity())
                    .restaurantId(dto.getRestaurantId())
                    .totalPrice(dto.getTotalPrice())
                    .build();

            inventoryReserveEventService.reserveInventory(reserveRequestEvent);

        } catch (RuntimeException ex) {
            log.error("❌ Payment processing failed for Order ID: {}. Reason: {}", dto.getOrderId(), ex.getMessage(), ex);
            publishPaymentFailure(dto.getOrderId(), ex.getMessage());
        } catch (Exception ex) {
            log.error("💥 Unexpected error during payment processing for Order ID: {}", dto.getOrderId(), ex);
            publishPaymentFailure(dto.getOrderId(), "Internal server error during payment processing");
        }
    }

    private void publishPaymentFailure(String orderId, String reason) {
        PaymentResponseEvent responseEvent = PaymentResponseEvent.builder()
                .orderId(orderId)
                .reason(reason)
                .success(false)
                .build();
        paymentFailedEventService.publishTransactionFailedEvent(responseEvent);
    }
}
