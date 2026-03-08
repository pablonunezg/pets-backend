package com.pumapunku.pet.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Unit tests for {@link BusinessException}.
 */
@DisplayName("BusinessException")
class BusinessExceptionTest
{
    @Test
    @DisplayName("should create exception with the received message")
    void constructor_debeGuardarMensaje()
    {
        BusinessException ex = new BusinessException("error de negocio");
        assertEquals("error de negocio", ex.getMessage());
    }

    @Test
    @DisplayName("debe ser RuntimeException")
    void debeExtenderRuntimeException()
    {
        assertInstanceOf(RuntimeException.class, new BusinessException("msg"));
    }
}
