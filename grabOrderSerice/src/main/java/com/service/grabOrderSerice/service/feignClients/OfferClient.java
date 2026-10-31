package com.service.grabOrderSerice.service.feignClients;

import com.service.grabOrderSerice.dto.FoodOffer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "offerService")
public interface OfferClient {
    @GetMapping("/api/v1/food-offers/{id}")
    public ResponseEntity<FoodOffer> getOfferById(@PathVariable String id) throws Exception;
}
