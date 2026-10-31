package com.service.restaurantService.service.impl;

import com.service.restaurantService.dto.UserResponseDto;
import com.service.restaurantService.entities.Restaurant;
import com.service.restaurantService.exception.ResourceNotFoundException;
import com.service.restaurantService.exception.UnauthorizedException;
import com.service.restaurantService.repo.RestaurantRepository;
import com.service.restaurantService.service.RestaurantService;
import com.service.restaurantService.service.feignClientServices.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserClient userClient;

    /**
     * ✅ Validate if the current user (from token) is the owner of the restaurant.
     */
    private void validateOwnership(String ownerId, String authHeader) {
        UserResponseDto dto = userClient.getUserHandler(authHeader);
        if (dto == null || dto.getUserId() == null) {
            throw new UnauthorizedException("Invalid user information. Please login again.");
        }

        if (!Objects.equals(dto.getUserId(), ownerId)) {
            throw new UnauthorizedException("You are not authorized to perform this action on this restaurant.");
        }
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant, String authHeader) throws Exception {
        UserResponseDto dto = userClient.getUserHandler(authHeader);

        if (dto == null || dto.getUserId() == null) {
            throw new UnauthorizedException("Invalid token or user not found.");
        }

        if (!Objects.equals(dto.getUserId(), restaurant.getOwner()) || !Objects.equals(dto.getRole(),"OWNER")) {
            throw new UnauthorizedException("You are not the owner of this restaurant.");
        }

        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant getRestaurantById(String id) throws Exception {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    @Override
    public List<Restaurant> getAllRestaurants() throws Exception {
        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant updateRestaurant(String id, Restaurant restaurant, String authHeader) throws Exception {
        Restaurant existing = getRestaurantById(id);
        validateOwnership(existing.getOwner(), authHeader);

        existing.setName(restaurant.getName());
        existing.setDescription(restaurant.getDescription());
        existing.setAddress(restaurant.getAddress());
        existing.setPhoneNumber(restaurant.getPhoneNumber());
        existing.setEmail(restaurant.getEmail());
        existing.setCuisineType(restaurant.getCuisineType());
        existing.setLatitude(restaurant.getLatitude());
        existing.setLongitude(restaurant.getLongitude());
        existing.setRating(restaurant.getRating());
        existing.setOpen(restaurant.isOpen());

        return restaurantRepository.save(existing);
    }

    @Override
    public void deleteRestaurant(String id, String authHeader) throws Exception {
        Restaurant existing = getRestaurantById(id);
        validateOwnership(existing.getOwner(), authHeader);
        restaurantRepository.deleteById(id);
    }

    @Override
    public List<Restaurant> getRestaurantsByOwner(String ownerId) throws Exception {
        return restaurantRepository.findByOwner(ownerId);
    }

    @Override
    public List<Restaurant> getRestaurantsByCuisine(String cuisineType) throws Exception {
        return restaurantRepository.findByCuisineType(cuisineType);
    }

    @Override
    public List<Restaurant> getOpenRestaurants() throws Exception {
        return restaurantRepository.findByIsOpenTrue();
    }

    @Override
    public Restaurant openRestaurant(String id, String authHeader) throws Exception {
        Restaurant restaurant = getRestaurantById(id);
        validateOwnership(restaurant.getOwner(), authHeader);
        restaurant.setOpen(true);
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant closeRestaurant(String id, String authHeader) throws Exception {
        Restaurant restaurant = getRestaurantById(id);
        validateOwnership(restaurant.getOwner(), authHeader);
        restaurant.setOpen(false);
        return restaurantRepository.save(restaurant);
    }
}
