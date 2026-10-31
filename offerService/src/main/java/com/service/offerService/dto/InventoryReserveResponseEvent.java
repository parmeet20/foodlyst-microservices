package com.service.offerService.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReserveResponseEvent {
    private String orderId;
    private String userId;
    private String foodOfferId;
    private int quantity;
    private String restaurantId;
    private double totalPrice;
}
