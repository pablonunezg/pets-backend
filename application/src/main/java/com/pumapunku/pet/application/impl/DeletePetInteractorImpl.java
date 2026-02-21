package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.DeletePetInteractor;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;


@Named
@RequiredArgsConstructor
public class DeletePetInteractorImpl implements DeletePetInteractor
{
    private final transient PetRepository petRepository;

    @Override
    public void execute(String id)
    {
        petRepository.delete(id);
    }
}
