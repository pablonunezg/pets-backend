package com.pumapunku.pet.domain.repository;

import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port for pet management in the domain layer.
 *
 * <p>Defines the contract that any pet persistence implementation must fulfill.
 * The application layer depends only on this interface, not on its concrete
 * infrastructure implementation, following the dependency inversion principle.</p>
 */
public interface PetRepository
{
    /**
     * Persists a new pet and returns it with its assigned identifier.
     *
     * @param pet pet data to create; must not be {@code null}.
     * @return the persisted pet with its generated {@code id}.
     */
    Pet create(Pet pet);

    /**
     * Updates the data of an existing pet.
     *
     * @param pet entity with the identifier and new data; must not be {@code null}.
     */
    void update(Pet pet);

    /**
     * Deletes the pet identified by the given UUID.
     *
     * @param id unique identifier of the pet to delete.
     * @throws com.pumapunku.pet.domain.exception.NotFoundException if no pet exists with that ID.
     */
    void delete(UUID id);

    /**
     * Retrieves a page of pets registered in the system.
     *
     * @param pageRequest pagination parameters (1-based page and size); must not be {@code null}.
     * @return {@link Page} with the list of pets for the requested page and pagination metadata.
     */
    Page<Pet> getPets(PageRequest pageRequest);

    /**
     * Finds a pet by its unique identifier.
     *
     * @param id unique identifier of the pet.
     * @return {@link Optional} containing the pet if found, or empty if not found.
     */
    Optional<Pet> findById(UUID id);
}
