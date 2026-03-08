package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.GetPetByIdInteractor;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.exception.NotFoundException;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Implementation of the {@link GetPetByIdInteractor} use case.
 *
 * <p>Delegates the search to the domain repository {@link PetRepository}.
 * Throws {@link NotFoundException} if the pet is not found.</p>
 */
@Named
@RequiredArgsConstructor
public class GetPetByIdInteractorImpl implements GetPetByIdInteractor
{
    /**
     * Domain repository for finding a pet by identifier.
     */
    private final transient PetRepository petRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Pet execute(UUID petId)
    {
        return petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException("Pet", petId.toString()));
    }
}
