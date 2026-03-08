package com.pumapunku.pet.application;

import com.pumapunku.pet.domain.Pet;

import java.util.UUID;

/**
 * Use case: retrieve a pet by its unique identifier.
 *
 * <p>Acts as an input port in the hexagonal architecture.</p>
 */
public interface GetPetByIdInteractor
{
    /**
     * Returns the pet associated with the given identifier.
     *
     * @param petId unique identifier of the pet; must not be {@code null}.
     * @return the found pet.
     * @throws com.pumapunku.pet.domain.exception.NotFoundException if no pet exists
     *                                                              with that identifier.
     */
    Pet execute(UUID petId);
}
