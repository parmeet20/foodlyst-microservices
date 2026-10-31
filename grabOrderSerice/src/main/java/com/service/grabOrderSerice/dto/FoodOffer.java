package com.service.grabOrderSerice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOffer {

    private String id;

    private String foodName;

    private String type;

    private Float latitude;

    private Float longitude;

    private Integer quantity;

    private Integer remainingQty;

    private Integer maxPerPerson;

    private Float perQtyPrice;

    private Boolean isActive = false;

    private LocalDateTime availableFrom;

    private LocalDateTime availableTo;

    private String restaurantId;

    private String owner;
}