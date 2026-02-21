package com.pumapunku.pet.infrastructure.repository.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pumapunku.pet.domain.AgeGroup;
import com.pumapunku.pet.domain.EnergyLevel;
import com.pumapunku.pet.domain.Gender;
import com.pumapunku.pet.domain.Size;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que representa a una mascota en la base de datos.
 *
 * <p>Mapeada a la tabla {@code pet}. Mantiene una relación {@code ManyToOne}
 * con {@link Refuge} a través de la columna {@code refuge_id}.</p>
 *
 * <p>El callback {@link #prePersist()} garantiza que el UUID y la fecha
 * de creación se asignen automáticamente si no han sido establecidos,
 * usando la estrategia UUIDv7 (ordenado por tiempo) para mejorar
 * el rendimiento de los índices de base de datos.</p>
 */
@Entity
@Table(name = "pet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetEntity
{
    /** Identificador único de la mascota (UUID v7). */
    @Id
    private UUID id;

    /** Nombre de la mascota; requerido. */
    @Column(nullable = false)
    private String name;

    /** URLs de imágenes de la mascota, separadas por comas; requerido. */
    @Column(nullable = false)
    private String picture;

    /** Raza del animal; requerido. */
    @Column(nullable = false)
    private String breed;

    /** Grupo de edad del animal; requerido. */
    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    private AgeGroup ageGroup;

    /** Tamaño del animal; requerido. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Size size;

    /** Género del animal; requerido. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    /** Indica si el animal ha sido esterilizado. */
    @Column(name = "is_neutered", nullable = false)
    private boolean isNeutered;

    /** Indica si el animal convive bien con otros perros. */
    @Column(name = "good_with_dogs", nullable = false)
    private boolean goodWithDogs;

    /** Indica si el animal convive bien con gatos. */
    @Column(name = "good_with_cats", nullable = false)
    private boolean goodWithCats;

    /** Indica si el animal convive bien con niños. */
    @Column(name = "good_with_kids", nullable = false)
    private boolean goodWithKids;

    /** Nivel de energía del animal; requerido. */
    @Enumerated(EnumType.STRING)
    @Column(name = "energy_level", nullable = false)
    private EnergyLevel energyLevel;

    /** Refugio al que pertenece la mascota; cargado de forma perezosa. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refuge_id", nullable = false)
    private Refuge refuge;

    /** Usuario que registró la mascota; cargado de forma perezosa. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Fecha y hora de registro de la mascota; no actualizable tras la creación. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Asigna automáticamente el UUID y la fecha de creación antes de la primera persistencia.
     * Usa UUIDv7 para garantizar el ordenamiento cronológico en la base de datos.
     */
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
    }
}
