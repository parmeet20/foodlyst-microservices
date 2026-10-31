package com.service.balanceService.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReserveRequestEvent {
    private String orderId;
    private String userId;
    private String restaurantId;
    private String foodOfferId;
    private int quantity;
    private double totalPrice;
}
