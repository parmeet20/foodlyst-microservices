package com.service.grabOrderSerice.service.impl;

import com.service.grabOrderSerice.dto.*;
import com.service.grabOrderSerice.entities.Rating;
import com.service.grabOrderSerice.repo.RatingRepo;
import com.service.grabOrderSerice.service.RatingService;
import com.service.grabOrderSerice.service.feignClients.*;
import com.service.grabOrderSerice.service.kafka.kafkaProducers.OrderReceivedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepo ratingRepo;
    private final OfferClient offerClient;
    private final UserClient userClient;
    private final RestaurantClient restaurantClient;
    private final OrderClient orderClient;
    private final Executor executor; // virtual-thread executor
    private final OrderReceivedProducer orderReceivedProducer;

    @Override
    public Rating grabAndRateRestaurant(Rating r, String authHeader) throws Exception {
        // Run the logic in a virtual thread using the injected Executor
        FutureTask<Rating> task = new FutureTask<>(() -> {

            // Safely fetch user
            UserResponseDto user = Optional.ofNullable(userClient.getUserHandler(authHeader))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Safely fetch restaurant
            Restaurant restaurant = Optional.ofNullable(
                            restaurantClient.getRestaurantById(r.getRestaurantId()))
                    .map(ResponseEntity::getBody)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            // Safely fetch order
            Order order = Optional.ofNullable(
                            orderClient.getOrderById(r.getOrderId()))
                    .map(ResponseEntity::getBody)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Safely fetch offer
            FoodOffer offer = Optional.ofNullable(
                            offerClient.getOfferById(r.getOfferId()))
                    .map(ResponseEntity::getBody)
                    .orElseThrow(() -> new RuntimeException("Offer not found"));

            // Validate all IDs
            if (!Objects.equals(r.getUserId(), user.getUserId()))
                throw new RuntimeException("Invalid user ID");
            if (!Objects.equals(r.getRestaurantId(), restaurant.getId()))
                throw new RuntimeException("Invalid restaurant ID");
            if (!Objects.equals(r.getOfferId(), offer.getId()))
                throw new RuntimeException("Invalid offer ID");
            if (!Objects.equals(r.getOrderId(), order.getId()))
                throw new RuntimeException("Invalid order ID");

            // Save and publish event
            Rating rating = Optional.of(r)
                    .map(ratingRepo::save)
                    .orElseThrow(() -> new RuntimeException("Failed to save rating"));

            orderReceivedProducer.publishTransactionFailedEvent(
                    new PaymentResponseEvent(rating.getOrderId(), true, "GRABBED SUCCESSFULLY")
            );

            System.out.println("Running on thread: " + Thread.currentThread());
            return rating;
        });

        // Submit the task to the virtual-thread executor and wait for the result
        executor.execute(task);
        return task.get(); // waits for the virtual thread to complete
    }
}
