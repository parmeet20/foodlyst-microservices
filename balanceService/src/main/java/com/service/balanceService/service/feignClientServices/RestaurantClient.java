package com.service.balanceService.service.feignClientServices;

import com.service.balanceService.dto.Restaurant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurantService")
public interface RestaurantClient {
    @GetMapping("/api/v1/restaurants/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable String id) throws Exception;
}
