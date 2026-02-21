package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.infrastructure.repository.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la entidad {@link PetEntity}.
 *
 * <p>Proporciona las operaciones CRUD estándar (save, findById, findAll,
 * deleteById, etc.) generadas automáticamente por Spring Data JPA.
 * Al ser de visibilidad de paquete ({@code package-private}), solo es
 * accesible desde {@link PetRepository}, respetando el principio de
 * encapsulamiento de la infraestructura.</p>
 */
interface PetRepositoryJPA extends JpaRepository<PetEntity, UUID>
{
}
