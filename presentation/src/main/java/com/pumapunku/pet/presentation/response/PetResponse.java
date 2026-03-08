package com.pumapunku.pet.presentation.response;

import com.pumapunku.pet.domain.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO with the public data of a pet.
 *
 * @param id           unique identifier.
 * @param name         name (max 50 characters).
 * @param description  description (max 500 characters).
 * @param picture      comma-separated image URLs.
 * @param breed        breed (max 100 characters).
 * @param ageGroup     age group.
 * @param petSize      body size.
 * @param gender       gender.
 * @param isNeutered   neutered.
 * @param goodWithDogs gets along with dogs.
 * @param goodWithCats gets along with cats.
 * @param goodWithKids gets along with children.
 * @param energyLevel  energy level.
 * @param status       current status of the pet.
 * @param userId       user who registered the pet.
 * @param createdAt    registration date.
 */
public record PetResponse(
        UUID id,
        String name,
        String description,
        String picture,
        String breed,
        AgeGroup ageGroup,
        PetSize petSize,
        Gender gender,
        boolean isNeutered,
        boolean goodWithDogs,
        boolean goodWithCats,
        boolean goodWithKids,
        EnergyLevel energyLevel,
        Status status,
        UUID userId,
        LocalDateTime createdAt
)
{
}
