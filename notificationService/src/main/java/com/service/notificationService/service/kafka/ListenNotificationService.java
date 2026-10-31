package com.service.notificationService.service.kafka;

import com.service.notificationService.dto.NotificationDto;
import com.service.notificationService.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListenNotificationService {
    private final EmailService emailService;

    @KafkaListener(topics = "notification.event", groupId = "notification.event.group")
    public void notificationListener(NotificationDto dto) {
        emailService.gmailSenderHandler(dto);
        log.info("Email sent successfully");
    }
}
