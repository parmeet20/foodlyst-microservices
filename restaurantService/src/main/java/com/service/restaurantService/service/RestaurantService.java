package com.service.restaurantService.service;

import com.service.restaurantService.entities.Restaurant;
import java.util.List;

public interface RestaurantService {

    Restaurant createRestaurant(Restaurant restaurant, String authHeader) throws Exception;

    Restaurant getRestaurantById(String id) throws Exception;

    List<Restaurant> getAllRestaurants() throws Exception;

    Restaurant updateRestaurant(String id, Restaurant restaurant, String authHeader) throws Exception;

    void deleteRestaurant(String id, String authHeader) throws Exception;

    List<Restaurant> getRestaurantsByOwner(String ownerId) throws Exception;

    List<Restaurant> getRestaurantsByCuisine(String cuisineType) throws Exception;

    List<Restaurant> getOpenRestaurants() throws Exception;

    Restaurant openRestaurant(String id, String authHeader) throws Exception;

    Restaurant closeRestaurant(String id, String authHeader) throws Exception;
}
