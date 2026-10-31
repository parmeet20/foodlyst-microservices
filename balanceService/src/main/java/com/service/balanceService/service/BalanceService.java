package com.service.balanceService.service;

import com.service.balanceService.entities.Balance;

public interface BalanceService {

    void createWallet(String userId);

    Balance getBalanceByUserId(String userId);

    Balance addFunds(String userId, double amount);

    Balance deductFunds(String userId, double amount);

    boolean transferFunds(String fromUserId, String toUserId, double amount) throws Exception;

    Balance connectWallet(String userId);

    Balance disconnectWallet(String userId);
}
