package com.service.transactionService.service;

import com.service.transactionService.dto.CreateTransactionDto;
import com.service.transactionService.entities.Transaction;

import java.util.List;

public interface TransactionService {
    void createTransaction(CreateTransactionDto dto);
    List<Transaction> getAllTransactions();
    Transaction getTransactionById(String id);
}
