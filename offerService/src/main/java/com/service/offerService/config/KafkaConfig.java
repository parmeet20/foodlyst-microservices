package com.service.offerService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic transactionSaveEvent() {
        return new NewTopic("transaction.save.event", 2, (short) 1);
    }

    @Bean
    public NewTopic transactionResponseEvent() {
        return new NewTopic("transaction.response.event", 2, (short) 1);
    }
}
