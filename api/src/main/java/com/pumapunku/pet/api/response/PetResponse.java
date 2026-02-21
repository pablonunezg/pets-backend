package com.pumapunku.pet.api.response;

import com.pumapunku.pet.domain.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PetResponse
{
    private String id;
    private String name;

    public static PetResponse from(Pet pet)
    {
        return new PetResponse(pet.getId(), pet.getName());
    }
}
