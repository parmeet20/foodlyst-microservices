package com.service.restaurantService.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurants")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String address;

    private String phoneNumber;

    private String email;

    @Column(nullable = false)
    private String cuisineType;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private String owner;

    private double rating;

    private boolean isOpen;

}
