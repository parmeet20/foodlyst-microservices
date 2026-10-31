package com.service.offerService.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDto {
    private String transactionId;
    private String orderId;
    private String sourceUserId;
    private String destUserId;
    private int qty;
    private Double amount;
    private String status; // INITIATED, SUCCESS, FAILED, REVERTED
}