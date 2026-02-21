package com.pumapunku.pet.api;

import com.pumapunku.pet.domain.exception.AlreadyExistsException;
import com.pumapunku.pet.domain.exception.BusinessException;
import com.pumapunku.pet.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PetApiExceptionHandlerTest
{
    @Test
    void testAlreadyExistsException()
    {
        PetApiExceptionHandler petApiExceptionHandler = new PetApiExceptionHandler();
        Map<String, String> responseMap = petApiExceptionHandler.exceptions(new AlreadyExistsException("pet", "12"));

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("message", "12 id already exists in the table");

        assertEquals(expectedMap, responseMap);
    }

    @Test
    void testNotFoundException()
    {
        PetApiExceptionHandler petApiExceptionHandler = new PetApiExceptionHandler();
        Map<String, String> responseMap = petApiExceptionHandler.exceptions(new NotFoundException("pet", "12"));

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("message", "12 not found!");

        assertEquals(expectedMap, responseMap);
    }

    @Test
    void testBusinessException()
    {
        PetApiExceptionHandler petApiExceptionHandler = new PetApiExceptionHandler();
        Map<String, String> responseMap = petApiExceptionHandler.exceptions(new BusinessException("invalid operation"));

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("message", "invalid operation");

        assertEquals(expectedMap, responseMap);
    }
}
