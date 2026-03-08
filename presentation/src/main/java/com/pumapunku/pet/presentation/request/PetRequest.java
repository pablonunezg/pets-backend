package com.pumapunku.pet.presentation.request;

import com.pumapunku.pet.domain.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Input DTO for creating or updating a pet.
 *
 * <p>Validation constraints reflect the DDL of the
 * {@code pet} table in {@code 1.0.0_database.xml}:</p>
 *
 * <ul>
 *   <li>{@code name}        — VARCHAR(50)  NOT NULL</li>
 *   <li>{@code description} — VARCHAR(500) NOT NULL</li>
 *   <li>{@code picture}     — VARCHAR(500) — managed by the server; not validated here.</li>
 *   <li>{@code breed}       — VARCHAR(100) NOT NULL</li>
 *   <li>{@code ageGroup}    — NOT NULL</li>
 *   <li>{@code size}        — NOT NULL</li>
 *   <li>{@code gender}      — NOT NULL</li>
 *   <li>{@code energyLevel} — NOT NULL</li>
 *   <li>{@code status}      — NOT NULL</li>
 * </ul>
 *
 * <p><strong>Note on {@code picture}:</strong> this field has no {@code @NotBlank}
 * because its value is always determined by the server:</p>
 * <ul>
 *   <li>On {@code POST}: derived from files uploaded to the bucket.</li>
 *   <li>On {@code PUT} with files: old files are deleted and new ones are uploaded.</li>
 *   <li>On {@code PUT} without files: the existing value in the database is kept.</li>
 * </ul>
 */
@NoArgsConstructor
@Getter
@Setter
public class PetRequest
{
    /**
     * Assigned internally on updates (PUT); not validated here.
     */
    private String id;

    /**
     * VARCHAR(50) NOT NULL
     */
    @NotBlank
    @Size(max = 50)
    private String name;

    /**
     * VARCHAR(500) NOT NULL
     */
    @NotBlank
    @Size(max = 500)
    private String description;

    /**
     * VARCHAR(500) — image file URL or name.
     * Managed by the server; the client does not need to send it.
     */
    @Size(max = 500)
    private String picture;

    /**
     * VARCHAR(100) NOT NULL
     */
    @NotBlank
    @Size(max = 100)
    private String breed;

    /**
     * NOT NULL
     */
    @NotNull
    private AgeGroup ageGroup;

    /**
     * NOT NULL
     */
    @NotNull
    private PetSize petSize;

    /**
     * NOT NULL
     */
    @NotNull
    private Gender gender;

    /**
     * BOOLEAN NOT NULL DEFAULT false
     */
    private boolean neutered;

    /**
     * BOOLEAN NOT NULL DEFAULT true
     */
    private boolean goodWithDogs;

    /**
     * BOOLEAN NOT NULL DEFAULT true
     */
    private boolean goodWithCats;

    /**
     * BOOLEAN NOT NULL DEFAULT true
     */
    private boolean goodWithKids;

    /**
     * NOT NULL
     */
    @NotNull
    private EnergyLevel energyLevel;

    /**
     * NOT NULL
     */
    @NotNull
    private Status status;
}
