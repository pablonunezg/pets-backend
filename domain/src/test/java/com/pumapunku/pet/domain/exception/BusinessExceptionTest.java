package com.pumapunku.pet.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BusinessExceptionTest
{
    @Test
    void createBussinesException()
    {
        BusinessException businessException = new BusinessException("my Business expception");

        assertEquals("my Business expception", businessException.getMessage());
    }
}
