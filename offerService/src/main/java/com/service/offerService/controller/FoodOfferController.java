package com.service.offerService.controller;

import com.service.offerService.dto.FoodOfferRequestDto;
import com.service.offerService.entities.FoodOffer;
import com.service.offerService.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/food-offers")
@RequiredArgsConstructor
public class FoodOfferController {

    private final OfferService offerService;

    @PostMapping
    public ResponseEntity<FoodOffer> createOffer(@Valid @RequestBody FoodOfferRequestDto dto, @RequestHeader("Authorization") String authHeader) throws Exception {
        return ResponseEntity.ok(offerService.createOffer(dto, authHeader));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodOffer> getOfferById(@PathVariable String id) throws Exception {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @GetMapping
    public ResponseEntity<List<FoodOffer>> getAllOffers() throws Exception {
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodOffer> updateOffer(@PathVariable String id,
                                                 @Valid @RequestBody FoodOfferRequestDto dto,
                                                 @RequestHeader("Authorization") String authHeader) throws Exception {
        return ResponseEntity.ok(offerService.updateOffer(id, dto, authHeader));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOffer(@PathVariable String id) throws Exception {
        offerService.deleteOffer(id);
        return ResponseEntity.ok("Offer deleted successfully");
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<FoodOffer> activateOffer(@PathVariable String id,@RequestHeader("Authorization") String authHeader) throws Exception {
        return ResponseEntity.ok(offerService.activateOffer(id,authHeader));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<FoodOffer> deactivateOffer(@PathVariable String id,@RequestHeader("Authorization") String authHeader) throws Exception {
        return ResponseEntity.ok(offerService.deactivateOffer(id,authHeader));
    }
}
