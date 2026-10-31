package com.service.notificationService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    private NewTopic newTopic() {
        return new NewTopic("notification.event", 4, (short) 1);
    }
}
