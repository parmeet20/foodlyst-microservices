package com.service.balanceService.service.impl;

import com.service.balanceService.dto.CreateTransactionDto;
import com.service.balanceService.entities.Balance;
import com.service.balanceService.repo.BalanceRepository;
import com.service.balanceService.service.BalanceService;
import com.service.balanceService.service.kafka.kafkaProducerService.PublishPaymentToTransaction;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BalanceServiceImpl implements BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private PublishPaymentToTransaction publishPaymentToTransaction;

    @Override
    public void createWallet(String userId) {
        Optional<Balance> existing = balanceRepository.findByUserId(userId);
        if (existing.isPresent()) {
            throw new RuntimeException("Wallet already exists for user: " + userId);
        }

        Balance wallet = Balance.builder()
                .userId(userId)
                .walletConnected(true)
                .balance(0.0)
                .build();

        balanceRepository.save(wallet);
    }

    @Override
    public Balance getBalanceByUserId(String userId) {
        return balanceRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
    }

    @Override
    public Balance addFunds(String userId, double amount) {
        Balance balance = getBalanceByUserId(userId);
        balance.setBalance(balance.getBalance() + amount);
        return balanceRepository.save(balance);
    }

    @Override
    public Balance deductFunds(String userId, double amount) {
        Balance balance = getBalanceByUserId(userId);

        if (balance.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance for user: " + userId);
        }

        balance.setBalance(balance.getBalance() - amount);
        return balanceRepository.save(balance);
    }

    @Override
    @Transactional
    public boolean transferFunds(String fromUserId, String toUserId, double amount) throws Exception {
        Balance srcAcc = getBalanceByUserId(fromUserId);
        Balance destAcc = getBalanceByUserId(toUserId);

        if (srcAcc.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance for transfer");
        }

        srcAcc.setBalance(srcAcc.getBalance() - amount);
        destAcc.setBalance(destAcc.getBalance() + amount);

        balanceRepository.save(srcAcc);
        balanceRepository.save(destAcc);
        CreateTransactionDto dto = CreateTransactionDto
                .builder()
                .sourceAccount(srcAcc.getUserId())
                .destinationAccount(destAcc.getUserId())
                .amount(amount)
                .qty(0)
                .build();
        publishPaymentToTransaction.createTransactionEntry(dto);
        return true;
    }

    @Override
    public Balance connectWallet(String userId) {
        Balance balance = getBalanceByUserId(userId);
        balance.setWalletConnected(true);
        return balanceRepository.save(balance);
    }

    @Override
    public Balance disconnectWallet(String userId) {
        Balance balance = getBalanceByUserId(userId);
        balance.setWalletConnected(false);
        return balanceRepository.save(balance);
    }
}
