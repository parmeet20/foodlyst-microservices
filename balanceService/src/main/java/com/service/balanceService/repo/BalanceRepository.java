package com.service.balanceService.repo;

import com.service.balanceService.entities.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, String> {
    Optional<Balance> findByUserId(String userId);
}