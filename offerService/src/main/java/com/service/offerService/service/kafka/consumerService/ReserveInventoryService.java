package com.service.offerService.service.kafka.consumerService;

import com.service.offerService.dto.InventoryReserveRequestEvent;
import com.service.offerService.dto.InventoryReserveResponseEvent;
import com.service.offerService.dto.TransactionRequestDto;
import com.service.offerService.entities.FoodOffer;
import com.service.offerService.exception.InvalidOfferOperationException;
import com.service.offerService.service.OfferService;
import com.service.offerService.service.kafka.producerService.InventoryReserveFailedEvent;
import com.service.offerService.service.kafka.producerService.PublishTransactionSuccessEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveInventoryService {

    private final OfferService offerService;
    private final PublishTransactionSuccessEventService publishTransactionSuccessEventService;
    private final InventoryReserveFailedEvent inventoryReserveFailedEvent;

    /**
     * Listens for inventory reservation events.
     * Reduces food offer quantity, publishes transaction success, or rollback on failure.
     */
    @KafkaListener(topics = "inventory.reserve.event", groupId = "inventory.reserve.event.group")
    public void reserveInventory(InventoryReserveRequestEvent reserveRequestEvent) {
        log.info("📦 Received inventory reservation request for FoodOffer ID: {}, Quantity: {}",
                reserveRequestEvent.getFoodOfferId(), reserveRequestEvent.getQuantity());

        try {
            // Fetch offer details
            FoodOffer offer = offerService.getOfferById(reserveRequestEvent.getFoodOfferId());

            // Validate offer
            if (!offer.getIsActive()) {
                throw new InvalidOfferOperationException("Offer is not active for reservation");
            }
            if (offer.getRemainingQty() < reserveRequestEvent.getQuantity()) {
                throw new InvalidOfferOperationException("Not enough quantity available to reserve");
            }

            // Deduct reserved quantity
            int updatedQty = offer.getRemainingQty() - reserveRequestEvent.getQuantity();
            offer.setRemainingQty(updatedQty);

            // Persist updated offer
            offerService.updateOfferQuantity(offer);

            // Build TransactionRequestDto
            TransactionRequestDto txEvent = TransactionRequestDto.builder()
                    .orderId(reserveRequestEvent.getOrderId())
                    .sourceUserId(reserveRequestEvent.getUserId())
                    .destUserId(offer.getOwner())
                    .qty(reserveRequestEvent.getQuantity())
                    .amount(reserveRequestEvent.getTotalPrice())
                    .status("SUCCESS")
                    .build();

            // Publish success event to Transaction Service
            publishTransactionSuccessEventService.publishTransactionToTxService(txEvent);

            log.info("✅ Successfully reserved {} units for Offer ID: {} | Remaining: {}",
                    reserveRequestEvent.getQuantity(), offer.getId(), offer.getRemainingQty());

        } catch (InvalidOfferOperationException e) {
            log.error("❌ Reservation failed for Offer ID: {} - Reason: {}",
                    reserveRequestEvent.getFoodOfferId(), e.getMessage());
    log.info(reserveRequestEvent.getUserId());
            // Build a rollback event
            InventoryReserveResponseEvent responseEvent = InventoryReserveResponseEvent.builder()
                    .orderId(reserveRequestEvent.getOrderId())
                    .userId(reserveRequestEvent.getUserId())
                    .foodOfferId(reserveRequestEvent.getFoodOfferId())
                    .quantity(reserveRequestEvent.getQuantity())
                    .restaurantId(reserveRequestEvent.getRestaurantId())
                    .totalPrice(reserveRequestEvent.getTotalPrice())
                    .build();
        log.info(responseEvent.toString());
            // Publish failure event for rollback
            inventoryReserveFailedEvent.revertPrevProcesses(responseEvent);

        } catch (Exception e) {
            log.error("⚠️ Unexpected error during inventory reservation: {}", e.getMessage(), e);

            // On unexpected failure, also send rollback
            InventoryReserveResponseEvent responseEvent = InventoryReserveResponseEvent.builder()
                    .orderId(reserveRequestEvent.getOrderId())
                    .userId(reserveRequestEvent.getUserId())
                    .foodOfferId(reserveRequestEvent.getFoodOfferId())
                    .quantity(reserveRequestEvent.getQuantity())
                    .restaurantId(reserveRequestEvent.getRestaurantId())
                    .totalPrice(reserveRequestEvent.getTotalPrice())
                    .build();

            inventoryReserveFailedEvent.revertPrevProcesses(responseEvent);
        }
    }
}
