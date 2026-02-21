package com.pumapunku.pet.infrastructure.firestore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PetCollectionTest
{
    @Test
    void createPetCollection()
    {
        PetCollection petCollection = new PetCollection("1", "Tammy");
        assertEquals("1", petCollection.getId());
        assertEquals("Tammy", petCollection.getName());
    }
}
