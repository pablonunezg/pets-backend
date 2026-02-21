package com.pumapunku.pet.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlreadyExistsExceptionTest
{
    @Test
    void createAlreadyExistsException()
    {
        AlreadyExistsException alreadyExistsException = new AlreadyExistsException("pet", "123");

        assertEquals("123 id already exists in the table", alreadyExistsException.getMessage());
        assertEquals("pet", alreadyExistsException.getCollectionName());
        assertEquals("123", alreadyExistsException.getId());
    }
}
