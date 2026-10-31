package com.service.balanceService.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String balanceId;
    private String userId;
    private Boolean walletConnected;
    private double balance;
}
