package com.pumapunku.pet.api.controller;

import com.pumapunku.pet.api.mapper.PetMapper;
import com.pumapunku.pet.api.request.PetRequest;
import com.pumapunku.pet.api.response.PetResponse;
import com.pumapunku.pet.application.CreatePetInteractor;
import com.pumapunku.pet.application.DeletePetInteractor;
import com.pumapunku.pet.application.UpdatePetInteractor;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/pet")
@RequiredArgsConstructor
public class PetController
{
    private final transient CreatePetInteractor createPetInteractor;
    private final transient UpdatePetInteractor updatePetInteractor;
    private final transient DeletePetInteractor deletePetInteractor;
    private final transient PetRepository petRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PetResponse> getPets()
    {
        List<Pet> pets = petRepository.getPets();

        return pets.stream().map(p -> PetMapper.INSTANCE.toPetResponse(p))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse create(@Valid @RequestBody PetRequest petRequest)
    {
        Pet pet = PetMapper.INSTANCE.toPet(petRequest);
        return PetResponse.from(createPetInteractor.execute(pet));
    }

    @PutMapping("{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String petId, @RequestBody PetRequest petRequest)
    {
        petRequest.setId(petId);
        Pet pet = PetMapper.INSTANCE.toPet(petRequest);
        updatePetInteractor.execute(pet);
    }

    @DeleteMapping("{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String petId)
    {
        deletePetInteractor.execute(petId);
    }
}
