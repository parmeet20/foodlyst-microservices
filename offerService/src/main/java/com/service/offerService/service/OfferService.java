package com.service.offerService.service;

import com.service.offerService.dto.FoodOfferRequestDto;
import com.service.offerService.entities.FoodOffer;

import java.util.List;

public interface OfferService {

    FoodOffer createOffer(FoodOfferRequestDto dto, String authHeaders) throws Exception;

    FoodOffer getOfferById(String id) throws Exception;

    List<FoodOffer> getAllOffers() throws Exception;

    FoodOffer updateOffer(String id, FoodOfferRequestDto dto, String authHeaders) throws Exception;

    void deleteOffer(String id) throws Exception;

    FoodOffer activateOffer(String id, String authHeaders) throws Exception;

    FoodOffer deactivateOffer(String id, String authHeaders) throws Exception;

    void updateOfferQuantity(FoodOffer offer) throws Exception;
}
