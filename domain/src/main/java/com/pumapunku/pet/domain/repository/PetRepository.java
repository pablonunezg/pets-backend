package com.pumapunku.pet.domain.repository;

import com.pumapunku.pet.domain.Pet;

import java.util.List;

public interface PetRepository
{
    Pet create(Pet pet);

    void update(Pet pet);

    void delete(String id);

    List<Pet> getPets();
}