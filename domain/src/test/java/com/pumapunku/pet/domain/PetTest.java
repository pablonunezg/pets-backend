package com.pumapunku.pet.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        assertNull(pet.getDescription());
        assertFalse(pet.isNeutered());
        assertNull(pet.getStatus());
    }

    @Test
    @DisplayName("constructor completo debe asignar todos los campos")
    void constructorCompleto_asignaTodosLosCampos()
    {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Pet pet = new Pet(id, "Luna", "Perrita juguetona", "url", "Labrador",
                AgeGroup.ADULT, PetSize.MEDIUM, Gender.FEMALE,
                true, false, true, true,
                EnergyLevel.HIGH, Status.FOR_ADOPTION, userId, now);

        assertEquals(id, pet.getId());
        assertEquals("Luna", pet.getName());
        assertEquals("Perrita juguetona", pet.getDescription());
        assertEquals("url", pet.getPicture());
        assertEquals("Labrador", pet.getBreed());
        assertEquals(AgeGroup.ADULT, pet.getAgeGroup());
        assertEquals(PetSize.MEDIUM, pet.getPetSize());
        assertEquals(Gender.FEMALE, pet.getGender());
        assertTrue(pet.isNeutered());
        assertFalse(pet.isGoodWithDogs());
        assertTrue(pet.isGoodWithCats());
        assertTrue(pet.isGoodWithKids());
        assertEquals(EnergyLevel.HIGH, pet.getEnergyLevel());
        assertEquals(Status.FOR_ADOPTION, pet.getStatus());
        assertEquals(userId, pet.getUserId());
        assertEquals(now, pet.getCreatedAt());
    }

    @Test
    @DisplayName("setters deben actualizar los campos correctamente")
    void setters_actualizanCampos()
    {
        Pet pet = new Pet();
        pet.setId(UUID.randomUUID());
        pet.setName("Max");
        pet.setDescription("Perro activo y amigable");
        pet.setNeutered(true);
        pet.setAgeGroup(AgeGroup.PUPPY);
        pet.setPetSize(PetSize.SMALL);
        pet.setGender(Gender.MALE);
        pet.setEnergyLevel(EnergyLevel.LOW);
        pet.setStatus(Status.MISSING);

        assertEquals("Max", pet.getName());
        assertEquals("Perro activo y amigable", pet.getDescription());
        assertTrue(pet.isNeutered());
        assertEquals(AgeGroup.PUPPY, pet.getAgeGroup());
        assertEquals(Status.MISSING, pet.getStatus());
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
