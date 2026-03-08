package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.GetPetsInteractor;
import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the {@link GetPetsInteractor} use case.
 *
 * <p>Delegates the paginated pet query to the domain repository
 * {@link PetRepository}, keeping the application logic separated
 * from persistence details.</p>
 *
 * <p>Registered as a Spring bean via {@code @Named} and receives
 * its dependencies by constructor via {@code @RequiredArgsConstructor}.</p>
 */
@Named
@RequiredArgsConstructor
public class GetPetsInteractorImpl implements GetPetsInteractor
{
    /**
     * Domain repository for accessing registered pets.
     */
    private final transient PetRepository petRepository;

    /**
     * {@inheritDoc}
     *
     * <p>Retrieves the page of pets by calling
     * {@link PetRepository#getPets(PageRequest)}.</p>
     */
    @Override
    public Page<Pet> execute(PageRequest pageRequest)
    {
        return petRepository.getPets(pageRequest);
    }
}
