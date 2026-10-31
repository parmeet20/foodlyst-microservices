package com.service.orderService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic paymentRequestEvent() {
        return new NewTopic("payment.request.event", 2, (short) 1);
    }
    @Bean
    public NewTopic paymentResponseEvent() {
        return new NewTopic("payment.response.event", 2, (short) 1);
    }
    @Bean
    public NewTopic sendNotificationEvent() {
        return new NewTopic("notification.event", 2, (short) 1);
    }
}
