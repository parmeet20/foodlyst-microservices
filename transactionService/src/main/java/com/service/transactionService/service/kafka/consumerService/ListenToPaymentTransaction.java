package com.service.transactionService.service.kafka.consumerService;

import com.service.transactionService.dto.CreateTransactionDto;
import com.service.transactionService.dto.PaymentResponseEvent;
import com.service.transactionService.dto.TransactionRequestDto;
import com.service.transactionService.service.TransactionService;
import com.service.transactionService.service.kafka.producerService.TransactionSuccessProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListenToPaymentTransaction {

    private final TransactionService transactionService;
    private final TransactionSuccessProducerService transactionSuccessProducerService;

    @KafkaListener(topics = "transaction.save.event", groupId = "transaction.save.event.group")
    public void listenToPayment(TransactionRequestDto dto) {
        log.info(dto.toString());
        transactionService.createTransaction(new CreateTransactionDto(dto.getSourceUserId(), dto.getDestUserId(), dto.getAmount(), dto.getQty()));
        transactionSuccessProducerService.txSuccessProducer(new PaymentResponseEvent(dto.getOrderId(), true, "transaction successful"));
    }
}
