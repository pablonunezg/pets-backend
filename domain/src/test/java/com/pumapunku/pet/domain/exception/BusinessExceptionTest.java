package com.pumapunku.pet.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link BusinessException}.
 */
@DisplayName("BusinessException")
class BusinessExceptionTest
{
    @Test
    @DisplayName("debe crear excepción con el mensaje recibido")
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
