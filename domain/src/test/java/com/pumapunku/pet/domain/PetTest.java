package com.pumapunku.pet.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio {@link Pet}.
 */
@DisplayName("Pet (dominio)")
class PetTest
{
    @Test
    @DisplayName("constructor sin argumentos debe crear objeto con valores nulos/false")
    void constructorVacio_valoresDefault()
    {
        Pet pet = new Pet();
        assertNull(pet.getId());
        assertNull(pet.getName());
        assertFalse(pet.isNeutered());
    }

    @Test
    @DisplayName("constructor completo debe asignar todos los campos")
    void constructorCompleto_asignaTodosLosCampos()
    {
        UUID id = UUID.randomUUID();
        UUID refugeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Pet pet = new Pet(id, "Luna", "url", "Labrador",
                AgeGroup.ADULT, Size.MEDIUM, Gender.FEMALE,
                true, false, true, true,
                EnergyLevel.HIGH, refugeId, userId, now);

        assertEquals(id, pet.getId());
        assertEquals("Luna", pet.getName());
        assertEquals("url", pet.getPicture());
        assertEquals("Labrador", pet.getBreed());
        assertEquals(AgeGroup.ADULT, pet.getAgeGroup());
        assertEquals(Size.MEDIUM, pet.getSize());
        assertEquals(Gender.FEMALE, pet.getGender());
        assertTrue(pet.isNeutered());
        assertFalse(pet.isGoodWithDogs());
        assertTrue(pet.isGoodWithCats());
        assertTrue(pet.isGoodWithKids());
        assertEquals(EnergyLevel.HIGH, pet.getEnergyLevel());
        assertEquals(refugeId, pet.getRefugeId());
        assertEquals(now, pet.getCreatedAt());
    }

    @Test
    @DisplayName("setters deben actualizar los campos correctamente")
    void setters_actualizanCampos()
    {
        Pet pet = new Pet();
        UUID id = UUID.randomUUID();
        pet.setId(id);
        pet.setName("Max");
        pet.setNeutered(true);
        pet.setAgeGroup(AgeGroup.PUPPY);
        pet.setSize(Size.SMALL);
        pet.setGender(Gender.MALE);
        pet.setEnergyLevel(EnergyLevel.LOW);

        assertEquals(id, pet.getId());
        assertEquals("Max", pet.getName());
        assertTrue(pet.isNeutered());
        assertEquals(AgeGroup.PUPPY, pet.getAgeGroup());
        assertEquals(Size.SMALL, pet.getSize());
        assertEquals(Gender.MALE, pet.getGender());
        assertEquals(EnergyLevel.LOW, pet.getEnergyLevel());
    }

    @Test
    @DisplayName("equals y hashCode deben basarse en todos los campos")
    void equalsYHashCode()
    {
        UUID id = UUID.randomUUID();
        Pet p1 = new Pet();
        p1.setId(id);
        p1.setName("Luna");

        Pet p2 = new Pet();
        p2.setId(id);
        p2.setName("Luna");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
