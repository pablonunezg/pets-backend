package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.CreatePetInteractor;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

@Named
@RequiredArgsConstructor
public class CreatePetInteractorImpl implements CreatePetInteractor
{
    private final transient PetRepository petRepository;

    @Override
    public Pet execute(Pet pet)
    {
        return petRepository.create(pet);
    }
}
