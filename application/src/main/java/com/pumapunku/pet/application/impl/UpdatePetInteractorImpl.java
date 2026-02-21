package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.UpdatePetInteractor;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

@Named
@RequiredArgsConstructor
public class UpdatePetInteractorImpl implements UpdatePetInteractor
{
    private final transient PetRepository petRepository;

    @Override
    public void execute(Pet pet)
    {
        petRepository.update(pet);
    }
}
