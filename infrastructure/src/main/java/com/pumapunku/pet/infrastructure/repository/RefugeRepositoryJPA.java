package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.infrastructure.repository.entity.Refuge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA repository for the {@link Refuge} entity.
 *
 * <p>Exposes standard CRUD operations on shelters, used internally by
 * {@link PetRepository} to validate shelter existence when creating a pet.
 * Package-private visibility limits direct access from outside the infrastructure.</p>
 */
interface RefugeRepositoryJPA extends JpaRepository<Refuge, UUID>
{
}
