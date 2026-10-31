package com.service.grabOrderSerice.controller;

import com.service.grabOrderSerice.entities.Rating;
import com.service.grabOrderSerice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rating")
@RequiredArgsConstructor
public class GrabOfferController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<Rating> rateHandler(@RequestBody Rating rating, @RequestHeader("Authorization") String authHeader) throws Exception {
        Rating r = ratingService.grabAndRateRestaurant(rating, authHeader);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }
}
