package com.pumapunku.pet.infrastructure.repository.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pumapunku.pet.domain.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "pet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetEntity
{
    @Id
    private UUID id;

    /**
     * VARCHAR(50) NOT NULL — pet name.
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * VARCHAR(500) NOT NULL — pet description.
     */
    @Column(nullable = false, length = 500)
    private String description;

    /**
     * VARCHAR(500) NOT NULL — comma-separated image URLs.
     */
    @Column(nullable = false, length = 500)
    private String picture;

    /**
     * VARCHAR(100) NOT NULL — animal breed.
     */
    @Column(nullable = false, length = 100)
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    private AgeGroup ageGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "pet_size", nullable = false)
    private PetSize petSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "is_neutered", nullable = false)
    private boolean isNeutered;

    @Column(name = "good_with_dogs", nullable = false)
    private boolean goodWithDogs;

    @Column(name = "good_with_cats", nullable = false)
    private boolean goodWithCats;

    @Column(name = "good_with_kids", nullable = false)
    private boolean goodWithKids;

    @Enumerated(EnumType.STRING)
    @Column(name = "energy_level", nullable = false)
    private EnergyLevel energyLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist()
    {
        if (id == null)
        {
            id = UuidCreator.getTimeOrderedEpoch();
        }
        if (createdAt == null)
        {
            createdAt = LocalDateTime.now();
        }

        if (picture.startsWith("baseUrl"))
        {
            List<String> pictureList = Arrays.stream(picture.split(","))
                    .collect(Collectors.toList());

            String baseUrl = pictureList.getFirst().replace("baseUrl", "");
            pictureList.removeFirst();

            picture = pictureList.stream()
                    .map(filename -> baseUrl + id + "_" + filename)
                    .collect(Collectors.joining(","));
        }
    }
}
