package com.service.offerService.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReserveRequestEvent {
    private String orderId;
    private String userId;
    private String foodOfferId;
    private String restaurantId;
    private int quantity;
    private double totalPrice;
}
