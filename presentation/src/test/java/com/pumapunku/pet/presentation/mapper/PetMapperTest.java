package com.pumapunku.pet.presentation.mapper;

import com.pumapunku.pet.domain.AgeGroup;
import com.pumapunku.pet.domain.EnergyLevel;
import com.pumapunku.pet.domain.Gender;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.Size;
import com.pumapunku.pet.presentation.request.PetRequest;
import com.pumapunku.pet.presentation.response.PetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link PetMapper} (capa de presentación).
 * Usa la instancia INSTANCE de MapStruct para validar el mapeo real.
 */
@DisplayName("PetMapper (presentación)")
class PetMapperTest
{
    private final PetMapper mapper = PetMapper.INSTANCE;

    @Test
    @DisplayName("toPet() debe mapear PetRequest a Pet ignorando createdAt y userId")
    void toPet_mapeaCorrectamente_ignoraCreatedAtYUserId()
    {
        UUID refugeId = UUID.randomUUID();
        PetRequest req = new PetRequest();
        req.setName("Luna");
        req.setPicture("img.jpg");
        req.setBreed("Labrador");
        req.setAgeGroup(AgeGroup.ADULT);
        req.setSize(Size.MEDIUM);
        req.setGender(Gender.FEMALE);
        req.setEnergyLevel(EnergyLevel.HIGH);
        req.setRefugeId(refugeId);
        req.setNeutered(true);
        req.setGoodWithDogs(false);
        req.setGoodWithCats(true);
        req.setGoodWithKids(true);

        Pet pet = mapper.toPet(req);

        assertEquals("Luna", pet.getName());
        assertEquals("img.jpg", pet.getPicture());
        assertEquals("Labrador", pet.getBreed());
        assertEquals(AgeGroup.ADULT, pet.getAgeGroup());
        assertEquals(Size.MEDIUM, pet.getSize());
        assertEquals(Gender.FEMALE, pet.getGender());
        assertEquals(EnergyLevel.HIGH, pet.getEnergyLevel());
        assertEquals(refugeId, pet.getRefugeId());
        assertTrue(pet.isNeutered());
        assertFalse(pet.isGoodWithDogs());
        assertTrue(pet.isGoodWithCats());
        assertTrue(pet.isGoodWithKids());
        assertNull(pet.getCreatedAt(), "createdAt debe ser null — ignorado en el mapeo");
        assertNull(pet.getUserId(), "userId debe ser null — ignorado en el mapeo");
    }

    @Test
    @DisplayName("toPetResponse() debe mapear todos los campos de Pet a PetResponse")
    void toPetResponse_mapeaTodosLosCampos()
    {
        UUID id       = UUID.randomUUID();
        UUID refugeId = UUID.randomUUID();
        UUID userId   = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Pet pet = new Pet(
                id, "Max", "img.jpg", "Beagle",
                AgeGroup.PUPPY, Size.SMALL, Gender.MALE,
                false, true, false, true,
                EnergyLevel.HIGH, refugeId, userId, now
        );

        PetResponse response = mapper.toPetResponse(pet);

        assertEquals(id, response.id());
        assertEquals("Max", response.name());
        assertEquals("img.jpg", response.picture());
        assertEquals("Beagle", response.breed());
        assertEquals(AgeGroup.PUPPY, response.ageGroup());
        assertEquals(Size.SMALL, response.size());
        assertEquals(Gender.MALE, response.gender());
        assertFalse(response.isNeutered());
        assertTrue(response.goodWithDogs());
        assertFalse(response.goodWithCats());
        assertTrue(response.goodWithKids());
        assertEquals(EnergyLevel.HIGH, response.energyLevel());
        assertEquals(refugeId, response.refugeId());
        assertEquals(userId, response.userId());
        assertEquals(now, response.createdAt());
    }
}
