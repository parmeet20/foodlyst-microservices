package com.service.offerService.entities;

import com.service.offerService.enums.FoodType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "food_offer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FoodType type;

    @Column(name = "latitude", nullable = false)
    private Float latitude;

    @Column(name = "longitude", nullable = false)
    private Float longitude;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "remaining_qty", nullable = false)
    private Integer remainingQty;

    @Column(name = "max_per_person", nullable = false)
    private Integer maxPerPerson;

    @Column(name = "per_qty_price", nullable = false)
    private Float perQtyPrice;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @Column(name = "available_to")
    private LocalDateTime availableTo;

    @Column(nullable = false)
    private String restaurantId;

    @Column(nullable = false)
    private String owner;
}
