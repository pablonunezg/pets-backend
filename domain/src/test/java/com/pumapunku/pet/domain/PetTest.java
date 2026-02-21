package com.pumapunku.pet.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PetTest
{
    @Test
    void petAllConstructor()
    {
        Pet pet = new Pet("1122", "Laika");

        assertEquals("1122", pet.getId());
        assertEquals("Laika", pet.getName());
    }
}
