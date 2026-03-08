package com.pumapunku.pet.presentation.mapper;

import com.pumapunku.pet.domain.*;
import com.pumapunku.pet.presentation.request.PetRequest;
import com.pumapunku.pet.presentation.response.PetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PetMapper (presentation)")
class PetMapperTest
{
    private final PetMapper mapper = PetMapper.INSTANCE;

    @Test
    @DisplayName("toPet() debe mapear PetRequest a Pet ignorando createdAt y userId")
    void toPet_mapeaCorrectamente()
    {
        PetRequest req = new PetRequest();
        req.setName("Luna");
        req.setDescription("Perrita amigable y juguetona");
        req.setPicture("img.jpg");
        req.setBreed("Labrador");
        req.setAgeGroup(AgeGroup.ADULT);
        req.setPetSize(PetSize.MEDIUM);
        req.setGender(Gender.FEMALE);
        req.setEnergyLevel(EnergyLevel.HIGH);
        req.setStatus(Status.FOR_ADOPTION);
        req.setNeutered(true);
        req.setGoodWithDogs(false);
        req.setGoodWithCats(true);
        req.setGoodWithKids(true);

        Pet pet = mapper.toPet(req);

        assertEquals("Luna", pet.getName());
        assertEquals("Perrita amigable y juguetona", pet.getDescription());
        assertEquals("img.jpg", pet.getPicture());
        assertEquals("Labrador", pet.getBreed());
        assertEquals(AgeGroup.ADULT, pet.getAgeGroup());
        assertEquals(PetSize.MEDIUM, pet.getPetSize());
        assertEquals(Gender.FEMALE, pet.getGender());
        assertEquals(EnergyLevel.HIGH, pet.getEnergyLevel());
        assertEquals(Status.FOR_ADOPTION, pet.getStatus());
        assertTrue(pet.isNeutered());
        assertFalse(pet.isGoodWithDogs());
        assertNull(pet.getCreatedAt(), "createdAt debe ser null — ignorado en el mapeo");
        assertNull(pet.getUserId(), "userId debe ser null — ignorado en el mapeo");
    }

    @Test
    @DisplayName("toPetResponse() debe mapear todos los campos de Pet a PetResponse")
    void toPetResponse_mapeaTodosLosCampos()
    {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Pet pet = new Pet(
                id, "Max", "Perro tranquilo", "img.jpg", "Beagle",
                AgeGroup.PUPPY, PetSize.SMALL, Gender.MALE,
                false, true, false, true,
                EnergyLevel.HIGH, Status.ADOPTED, userId, now
        );

        PetResponse response = mapper.toPetResponse(pet);

        assertEquals(id, response.id());
        assertEquals("Max", response.name());
        assertEquals("Perro tranquilo", response.description());
        assertEquals("img.jpg", response.picture());
        assertEquals("Beagle", response.breed());
        assertEquals(AgeGroup.PUPPY, response.ageGroup());
        assertEquals(PetSize.SMALL, response.petSize());
        assertEquals(Gender.MALE, response.gender());
        assertFalse(response.isNeutered());
        assertTrue(response.goodWithDogs());
        assertEquals(EnergyLevel.HIGH, response.energyLevel());
        assertEquals(Status.ADOPTED, response.status());
        assertEquals(userId, response.userId());
        assertEquals(now, response.createdAt());
    }
}
