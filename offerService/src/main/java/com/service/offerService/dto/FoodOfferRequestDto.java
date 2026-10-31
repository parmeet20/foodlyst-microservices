package com.service.offerService.dto;

import com.service.offerService.enums.FoodType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOfferRequestDto {

    @NotBlank(message = "Food name is required")
    private String foodName;

    @NotNull(message = "Food type is required")
    private FoodType type;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Float latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Float longitude;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Max per person is required")
    @Min(value = 1, message = "Max per person must be at least 1")
    private Integer maxPerPerson;

    @NotNull(message = "Price per quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Float perQtyPrice;

    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotBlank(message = "Owner ID is required")
    private String owner;
}
