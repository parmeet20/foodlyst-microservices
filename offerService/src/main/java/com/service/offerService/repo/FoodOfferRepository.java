package com.service.offerService.repo;

import com.service.offerService.entities.FoodOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodOfferRepository extends JpaRepository<FoodOffer, String> {
}