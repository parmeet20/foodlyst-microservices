package com.service.grabOrderSerice.service;

import com.service.grabOrderSerice.entities.Rating;

public interface RatingService {
    Rating grabAndRateRestaurant(Rating r,String authHeader) throws Exception;
}
