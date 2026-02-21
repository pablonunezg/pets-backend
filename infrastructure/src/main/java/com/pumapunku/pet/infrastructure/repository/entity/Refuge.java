package com.pumapunku.pet.infrastructure.repository.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad JPA que representa un refugio de animales en la base de datos.
 *
 * <p>Es la entidad raíz de la relación {@code OneToMany} con {@link PetEntity}.
 * Los métodos de ayuda {@link #addPet} y {@link #removePet} garantizan
 * la consistencia bidireccional de la relación.</p>
 *
 * <p>El identificador UUID se genera con la estrategia UUIDv7
 * (ordenado por tiempo) en el callback {@link #generateId()}.</p>
 */
@Entity
@Table(name = "refuge")
@Data
public class Refuge
{
    /** Identificador único del refugio (UUID v7). */
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** Nombre único del refugio; longitud máxima de 50 caracteres. */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** Descripción del refugio; longitud máxima de 500 caracteres. */
    @Column(nullable = false, length = 500)
    private String description;

    /** Lista de mascotas asociadas a este refugio. */
    @OneToMany(mappedBy = "refuge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetEntity> pets = new ArrayList<>();

    /**
     * Genera un UUID v7 para el identificador si aún no se ha asignado uno.
     * Invocado automáticamente por JPA antes de la primera persistencia.
     */
    @PrePersist
    public void generateId()
    {
        if (id == null)
        {
            id = UuidCreator.getTimeOrderedEpoch();
        }
    }

    /**
     * Agrega una mascota al refugio y establece la referencia bidireccional.
     *
     * @param pet mascota a asociar al refugio.
     */
    public void addPet(PetEntity pet)
    {
        pets.add(pet);
        pet.setRefuge(this);
    }

    /**
     * Desvincula una mascota del refugio y elimina la referencia bidireccional.
     *
     * @param pet mascota a desvincular.
     */
    public void removePet(PetEntity pet)
    {
        pets.remove(pet);
        pet.setRefuge(null);
    }
}
