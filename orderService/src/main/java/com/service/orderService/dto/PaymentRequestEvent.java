package com.service.orderService.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestEvent {
    private String orderId;
    private String userId;
    private String restaurantId;
    private double totalPrice;
    private int quantity;
    private String foodOfferId;
}
