package com.service.offerService.service.impl;

import com.service.offerService.dto.FoodOfferRequestDto;
import com.service.offerService.dto.Restaurant;
import com.service.offerService.dto.UserResponseDto;
import com.service.offerService.entities.FoodOffer;
import com.service.offerService.exception.InvalidOfferOperationException;
import com.service.offerService.exception.OfferNotFoundException;
import com.service.offerService.repo.FoodOfferRepository;
import com.service.offerService.service.OfferService;
import com.service.offerService.service.feignClientServices.RestaurantClient;
import com.service.offerService.service.feignClientServices.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OfferServiceImpl implements OfferService {

    private final FoodOfferRepository repository;
    private final RestaurantClient restaurantClient;
    private final UserClient userClient;

    @Override
    public FoodOffer createOffer(FoodOfferRequestDto dto, String authHeaders) throws Exception {
        validateOfferRequest(dto);
        log.info("Validating token: {}", authHeaders);

        UserResponseDto user = validateUser(authHeaders);
        log.info("User validated: {}", user.getUserId());

        if (!user.getUserId().equals(dto.getOwner())) {
            throw new InvalidOfferOperationException("Owner ID does not match the authenticated user");
        }

        Restaurant restaurant = validateRestaurant(dto.getRestaurantId());
        log.info("Restaurant validated: {}", restaurant.getId());

        if (!restaurant.getOwner().equals(user.getUserId()) || !Objects.equals(user.getRole(), "OWNER")) {
            throw new InvalidOfferOperationException("You are not authorized to create offers for this restaurant");
        }

        FoodOffer offer = FoodOffer.builder()
                .foodName(dto.getFoodName())
                .type(dto.getType())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .quantity(dto.getQuantity())
                .remainingQty(dto.getQuantity())
                .maxPerPerson(dto.getMaxPerPerson())
                .perQtyPrice(dto.getPerQtyPrice())
                .availableFrom(dto.getAvailableFrom())
                .availableTo(dto.getAvailableTo())
                .restaurantId(dto.getRestaurantId())
                .owner(dto.getOwner())
                .isActive(false)
                .build();

        try {
            return repository.save(offer);
        } catch (DataAccessException ex) {
            throw new InvalidOfferOperationException("Failed to create offer: " + ex.getMessage());
        }
    }

    @Override
    public FoodOffer getOfferById(String id) throws Exception {
        return repository.findById(id)
                .orElseThrow(() -> new OfferNotFoundException("Offer not found with id: " + id));
    }

    @Override
    public List<FoodOffer> getAllOffers() throws Exception {
        return repository.findAll();
    }

    @Override
    public FoodOffer updateOffer(String id, FoodOfferRequestDto dto, String authHeaders) throws Exception {
        validateOfferRequest(dto);

        FoodOffer offer = getOfferById(id);
        UserResponseDto user = validateUser(authHeaders);
        Restaurant restaurant = validateRestaurant(dto.getRestaurantId());

        if (!restaurant.getOwner().equals(user.getUserId())) {
            throw new InvalidOfferOperationException("You are not authorized to update this offer");
        }

        offer.setFoodName(dto.getFoodName());
        offer.setType(dto.getType());
        offer.setLatitude(dto.getLatitude());
        offer.setLongitude(dto.getLongitude());
        offer.setQuantity(dto.getQuantity());
        offer.setRemainingQty(dto.getQuantity());
        offer.setMaxPerPerson(dto.getMaxPerPerson());
        offer.setPerQtyPrice(dto.getPerQtyPrice());
        offer.setAvailableFrom(dto.getAvailableFrom());
        offer.setAvailableTo(dto.getAvailableTo());
        offer.setRestaurantId(dto.getRestaurantId());
        offer.setOwner(dto.getOwner());

        try {
            return repository.save(offer);
        } catch (DataAccessException ex) {
            throw new InvalidOfferOperationException("Failed to update offer: " + ex.getMessage());
        }
    }

    @Override
    public void deleteOffer(String id) throws Exception {
        FoodOffer offer = getOfferById(id);
        try {
            repository.delete(offer);
        } catch (DataAccessException ex) {
            throw new InvalidOfferOperationException("Failed to delete offer: " + ex.getMessage());
        }
    }

    @Override
    public FoodOffer activateOffer(String id, String authHeaders) throws Exception {
        UserResponseDto user = validateUser(authHeaders);
        FoodOffer offer = getOfferById(id);

        if (!offer.getOwner().equals(user.getUserId())) {
            throw new InvalidOfferOperationException("You are not authorized to activate this offer");
        }

        if (offer.getIsActive()) {
            throw new InvalidOfferOperationException("Offer is already active");
        }

        offer.setIsActive(true);
        return repository.save(offer);
    }

    @Override
    public FoodOffer deactivateOffer(String id, String authHeaders) throws Exception {
        UserResponseDto user = validateUser(authHeaders);
        FoodOffer offer = getOfferById(id);

        if (!offer.getOwner().equals(user.getUserId())) {
            throw new InvalidOfferOperationException("You are not authorized to deactivate this offer");
        }

        if (!offer.getIsActive()) {
            throw new InvalidOfferOperationException("Offer is already inactive");
        }

        offer.setIsActive(false);
        return repository.save(offer);
    }
    @Override
    public void updateOfferQuantity(FoodOffer offer) throws Exception {
        try {
            repository.save(offer);
        } catch (DataAccessException ex) {
            throw new InvalidOfferOperationException("Failed to update offer quantity: " + ex.getMessage());
        }
    }

    // ------------------- Helper Methods ------------------- //

    private void validateOfferRequest(FoodOfferRequestDto dto) {
        if (dto.getAvailableFrom() != null && dto.getAvailableTo() != null &&
                dto.getAvailableFrom().isAfter(dto.getAvailableTo())) {
            throw new InvalidOfferOperationException("availableFrom cannot be after availableTo");
        }
        if (dto.getQuantity() <= 0) {
            throw new InvalidOfferOperationException("Quantity must be greater than 0");
        }
        if (dto.getMaxPerPerson() <= 0) {
            throw new InvalidOfferOperationException("maxPerPerson must be greater than 0");
        }
        if (dto.getPerQtyPrice() < 0) {
            throw new InvalidOfferOperationException("perQtyPrice cannot be negative");
        }
    }

    private UserResponseDto validateUser(String authHeader) throws Exception {
        try {
            if (authHeader == null || authHeader.isBlank()) {
                throw new InvalidOfferOperationException("Authorization header is missing");
            }
            return userClient.getUserHandler(authHeader);
        } catch (Exception e) {
            log.error("User validation failed: {}", e.getMessage());
            throw new InvalidOfferOperationException("Invalid or unauthorized user token");
        }
    }

    private Restaurant validateRestaurant(String restaurantId) throws Exception {
        try {
            ResponseEntity<Restaurant> response = restaurantClient.getRestaurantById(restaurantId);
            if (response.getBody() == null) {
                throw new InvalidOfferOperationException("Restaurant not found");
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("Restaurant validation failed: {}", e.getMessage());
            throw new InvalidOfferOperationException("Failed to fetch restaurant: " + e.getMessage());
        }
    }
}
