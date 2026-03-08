package com.pumapunku.pet.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet
{
    private UUID id;

    /**
     * Pet name. Max 50 characters (DDL: VARCHAR(50) NOT NULL).
     */
    private String name;

    /**
     * Pet description. Max 500 characters (DDL: VARCHAR(500) NOT NULL).
     */
    private String description;

    /**
     * Comma-separated image URLs. Max 500 characters (DDL: VARCHAR(500) NOT NULL).
     */
    private String picture;

    /**
     * Animal breed. Max 100 characters (DDL: VARCHAR(100) NOT NULL).
     */
    private String breed;

    private AgeGroup ageGroup;
    private PetSize petSize;
    private Gender gender;

    /**
     * Indicates whether the animal has been neutered (DDL: BOOLEAN NOT NULL DEFAULT false).
     */
    private boolean neutered;

    private boolean goodWithDogs;
    private boolean goodWithCats;
    private boolean goodWithKids;
    private EnergyLevel energyLevel;

    /**
     * Current status of the pet (DDL: VARCHAR(20) NOT NULL).
     */
    private Status status;

    private UUID userId;
    private LocalDateTime createdAt;
}
