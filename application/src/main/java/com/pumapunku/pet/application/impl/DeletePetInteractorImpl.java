package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.DeletePetInteractor;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Implementation of the {@link DeletePetInteractor} use case.
 *
 * <p>Delegates pet deletion to the domain repository {@link PetRepository},
 * keeping application logic free from infrastructure dependencies.</p>
 *
 * <p>The bean is registered in Spring via {@code @Named}
 * and injected by constructor.</p>
 */
@Named
@RequiredArgsConstructor
public class DeletePetInteractorImpl implements DeletePetInteractor
{
    /**
     * Domain repository for deleting a pet by identifier.
     */
    private final transient PetRepository petRepository;

    /**
     * {@inheritDoc}
     *
     * <p>Calls {@link PetRepository#delete(UUID)} with the received identifier.</p>
     */
    @Override
    public void execute(UUID id)
    {
        petRepository.delete(id);
    }
}
