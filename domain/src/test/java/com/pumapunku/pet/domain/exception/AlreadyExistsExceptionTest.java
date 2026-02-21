package com.pumapunku.pet.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link AlreadyExistsException}.
 */
@DisplayName("AlreadyExistsException")
class AlreadyExistsExceptionTest
{
    @Test
    @DisplayName("debe guardar el mensaje recibido")
    void constructor_debeGuardarMensaje()
    {
        AlreadyExistsException ex = new AlreadyExistsException("ya existe");
        assertEquals("ya existe", ex.getMessage());
    }

    @Test
    @DisplayName("debe extender BusinessException")
    void debeExtenderBusinessException()
    {
        assertInstanceOf(BusinessException.class, new AlreadyExistsException("dup"));
    }
}
