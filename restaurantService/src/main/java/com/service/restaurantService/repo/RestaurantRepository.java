package com.service.restaurantService.repo;

import com.service.restaurantService.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    List<Restaurant> findByOwner(String owner);
    List<Restaurant> findByCuisineType(String cuisineType);
    List<Restaurant> findByIsOpenTrue();
    void deleteByOwner(String ownerId);
}