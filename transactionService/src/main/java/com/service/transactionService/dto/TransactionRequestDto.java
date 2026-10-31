package com.service.transactionService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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