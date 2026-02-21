package com.pumapunku.pet.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link NotFoundException}.
 */
@DisplayName("NotFoundException")
class NotFoundExceptionTest
{
    @Test
    @DisplayName("debe construir mensaje con recurso e id")
    void constructor_debeConstruirMensajeCorrectamente()
    {
        NotFoundException ex = new NotFoundException("Pet", "abc-123");
        assertEquals("Pet with id abc-123 not found", ex.getMessage());
    }

    @Test
    @DisplayName("debe extender BusinessException")
    void debeExtenderBusinessException()
    {
        assertInstanceOf(BusinessException.class, new NotFoundException("X", "1"));
    }

    @Test
    @DisplayName("debe ser RuntimeException")
    void debeExtenderRuntimeException()
    {
        assertInstanceOf(RuntimeException.class, new NotFoundException("X", "1"));
    }
}
