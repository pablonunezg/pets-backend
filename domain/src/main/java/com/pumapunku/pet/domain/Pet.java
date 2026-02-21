package com.pumapunku.pet.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio que representa una mascota en el sistema.
 *
 * <p>Contiene todos los atributos descriptivos de un animal que puede
 * ser dado en adopción desde un refugio. Es el objeto central del dominio
 * y no tiene dependencias con ninguna librería de infraestructura.</p>
 *
 * <p>El campo {@code refugeId} actúa como referencia al refugio propietario,
 * evitando una dependencia directa con la entidad {@code Refuge} de infraestructura.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet
{
    /** Identificador único de la mascota (UUID v7 ordenado por tiempo). */
    private UUID id;

    /** Nombre de la mascota. */
    private String name;

    /** URLs de las imágenes de la mascota, separadas por comas. */
    private String picture;

    /** Raza del animal. */
    private String breed;

    /** Grupo de edad de la mascota. */
    private AgeGroup ageGroup;

    /** Tamaño del animal. */
    private Size size;

    /** Género del animal. */
    private Gender gender;

    /** Indica si el animal ha sido esterilizado. */
    private boolean isNeutered;

    /** Indica si el animal convive bien con otros perros. */
    private boolean goodWithDogs;

    /** Indica si el animal convive bien con gatos. */
    private boolean goodWithCats;

    /** Indica si el animal convive bien con niños. */
    private boolean goodWithKids;

    /** Nivel de energía del animal. */
    private EnergyLevel energyLevel;

    /** Identificador del refugio al que pertenece esta mascota. */
    private UUID refugeId;

    /** Identificador del usuario que registró esta mascota (FK → app_user). */
    private UUID userId;

    /** Fecha y hora de registro de la mascota en el sistema. */
    private LocalDateTime createdAt;
}
