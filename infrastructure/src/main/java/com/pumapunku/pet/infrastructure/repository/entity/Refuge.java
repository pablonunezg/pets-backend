package com.pumapunku.pet.infrastructure.repository.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * JPA entity representing an animal shelter in the database.
 *
 * <p>Mapped to the {@code refuge} table. The {@code refuge_id} column was
 * removed from the {@code pet} table, so the bidirectional relationship
 * with pets no longer exists at the persistence layer.</p>
 */
@Entity
@Table(name = "refuge")
@Data
public class Refuge
{
    /**
     * Unique identifier of the shelter (UUID v7).
     */
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    /**
     * Unique shelter name; maximum length of 50 characters.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Shelter description; maximum length of 500 characters.
     */
    @Column(nullable = false, length = 500)
    private String description;

    /**
     * Generates a UUID v7 for the identifier if one has not been assigned yet.
     * Automatically invoked by JPA before the first persistence.
     */
    @PrePersist
    public void generateId()
    {
        if (id == null)
        {
            id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
