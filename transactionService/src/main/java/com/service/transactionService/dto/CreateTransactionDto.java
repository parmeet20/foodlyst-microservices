package com.service.transactionService.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransactionDto {
    private String sourceAccount;    // Changed from 'from'
    private String destinationAccount; // Changed from 'to'
    private double amount;
    private int qty;


    @Override
    public String toString() {
        return String.format("CreateTransactionDto{sourceAccount='%s', destinationAccount='%s', amount=%.2f, qty=%d}",
                sourceAccount, destinationAccount, amount, qty);
    }
}