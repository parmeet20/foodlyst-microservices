package com.service.transactionService.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    private String id;

    @Column(name = "source_account")  // Changed from 'from'
    private String sourceAccount;

    @Column(name = "destination_account")  // Changed from 'to'
    private String destinationAccount;

    private double amount;
    private int qty;

    private String orderId;

    @Column(name = "created_at")  // Changed from 'timestamp'
    private LocalDateTime createdAt;
}