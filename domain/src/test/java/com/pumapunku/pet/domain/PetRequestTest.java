package com.pumapunku.pet.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PetRequestTest
{
    @Test
    void createAlreadyExistsException()
    {
        PageRequest pageRequest = new PageRequest(22, 100);

        assertEquals(2200, pageRequest.offset());
    }
}
