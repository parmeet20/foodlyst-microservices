package com.service.balanceService.service.kafka.kafkaListenerService;

import com.service.balanceService.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class createCorrespondingBalanceAccount {
    public final BalanceService balanceService;
    @KafkaListener(topics = "create-balance-account-topic", groupId = "balance-group",containerFactory = "stringKafkaListenerContainerFactory")
    public void listenForUserIdToCreateBalanceAccount(String userId) {
        balanceService.createWallet(userId);
        System.out.println("✅ Created wallet for userId: " + userId);
    }
}
