package com.service.restaurantService.controller;

import com.service.restaurantService.entities.Restaurant;
import com.service.restaurantService.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // ✅ Create a new restaurant
    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(
            @RequestBody Restaurant restaurant,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        Restaurant createdRestaurant = restaurantService.createRestaurant(restaurant, authHeader);
        return ResponseEntity.ok(createdRestaurant);
    }

    // ✅ Get restaurant by ID
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable String id) throws Exception {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    // ✅ Get all restaurants
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() throws Exception {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    // ✅ Update restaurant by ID
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable String id,
            @RequestBody Restaurant restaurant,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        Restaurant updatedRestaurant = restaurantService.updateRestaurant(id, restaurant, authHeader);
        return ResponseEntity.ok(updatedRestaurant);
    }

    // ✅ Delete restaurant by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        restaurantService.deleteRestaurant(id, authHeader);
        return ResponseEntity.noContent().build();
    }

    // ✅ Get restaurants by ownerId
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Restaurant>> getRestaurantsByOwner(@PathVariable String ownerId) throws Exception {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByOwner(ownerId);
        return ResponseEntity.ok(restaurants);
    }

    // ✅ Get restaurants by cuisine type
    @GetMapping("/cuisine/{cuisineType}")
    public ResponseEntity<List<Restaurant>> getRestaurantsByCuisine(@PathVariable String cuisineType) throws Exception {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByCuisine(cuisineType);
        return ResponseEntity.ok(restaurants);
    }

    // ✅ Get only open restaurants
    @GetMapping("/open")
    public ResponseEntity<List<Restaurant>> getOpenRestaurants() throws Exception {
        List<Restaurant> restaurants = restaurantService.getOpenRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    // ✅ Open restaurant (owner action)
    @PutMapping("/{id}/open")
    public ResponseEntity<Restaurant> openRestaurant(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        Restaurant updated = restaurantService.openRestaurant(id, authHeader);
        return ResponseEntity.ok(updated);
    }

    // ✅ Close restaurant (owner action)
    @PutMapping("/{id}/close")
    public ResponseEntity<Restaurant> closeRestaurant(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        Restaurant updated = restaurantService.closeRestaurant(id, authHeader);
        return ResponseEntity.ok(updated);
    }
}
