package com.service.transactionService.service.impl;

import com.service.transactionService.dto.CreateTransactionDto;
import com.service.transactionService.entities.Transaction;
import com.service.transactionService.repo.TransactionRepository;
import com.service.transactionService.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public void createTransaction(CreateTransactionDto dto) {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .sourceAccount(dto.getSourceAccount())
                .destinationAccount(dto.getDestinationAccount())
                .amount(dto.getAmount())
                .qty(dto.getQty())
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction getTransactionById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }
}