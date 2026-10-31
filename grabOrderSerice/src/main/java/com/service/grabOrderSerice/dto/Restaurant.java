package com.service.grabOrderSerice.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Restaurant {

    private String id;
    private String name;
    private String description;
    private String address;
    private String phoneNumber;
    private String email;
    private String cuisineType;
    private double latitude;
    private double longitude;
    private String owner;
    private double rating;
    private boolean isOpen;
}