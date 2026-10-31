package com.service.restaurantService.service.kafka;

import com.service.restaurantService.dto.DeleteRestaurantEventDto;
import com.service.restaurantService.repo.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ListenDeleteRestaurantEvent {
    private final RestaurantRepository restaurantRepository;

    @KafkaListener(topics = "transaction.save.event", groupId = "transaction.save.event.group")
    public void listenToPayment(DeleteRestaurantEventDto dto) {
        try {
            log.info("Received delete event for userId: {}", dto.getUserId());
            restaurantRepository.deleteByOwner(dto.getUserId());
            log.info("Deleted restaurants for owner: {}", dto.getUserId());
        } catch (Exception e) {
            log.error("Error occurred while deleting restaurants for owner: {}", dto.getUserId(), e);
        }
    }
}
