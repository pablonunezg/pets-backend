package com.pumapunku.pet.application;

import java.util.UUID;

/**
 * Use case: delete a pet from the system.
 *
 * <p>Defines the contract for implementations that permanently remove
 * a pet identified by its UUID.</p>
 *
 * <p>Acts as an input port in the hexagonal architecture.</p>
 */
public interface DeletePetInteractor
{
    /**
     * Deletes the pet associated with the given identifier.
     *
     * @param petId unique identifier of the pet to delete; must not be {@code null}.
     * @throws com.pumapunku.pet.domain.exception.NotFoundException if no pet exists
     *                                                              with that identifier.
     */
    void execute(UUID petId);
}
