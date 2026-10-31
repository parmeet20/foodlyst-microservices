package com.service.transactionService.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Order {

    private String id;

    private String userId;
    private String restaurantId;
    private String foodOfferRequestId;

    private int quantity;
    private double totalPrice;

    private String status;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String token;

}
