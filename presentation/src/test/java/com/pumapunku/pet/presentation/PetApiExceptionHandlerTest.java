package com.pumapunku.pet.presentation;

import com.pumapunku.pet.domain.exception.AlreadyExistsException;
import com.pumapunku.pet.domain.exception.BusinessException;
import com.pumapunku.pet.domain.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link PetApiExceptionHandler}.
 */
@DisplayName("PetApiExceptionHandler")
class PetApiExceptionHandlerTest
{
    private final PetApiExceptionHandler handler = new PetApiExceptionHandler();

    @Test
    @DisplayName("NotFoundException - debe retornar mapa con mensaje de la excepción")
    void handleNotFoundException_retornaMensaje()
    {
        NotFoundException ex = new NotFoundException("Pet", "abc-123");

        Map<String, String> result = handler.handleNotFoundException(ex);

        assertEquals("Pet with id abc-123 not found", result.get("message"));
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("AlreadyExistsException - debe retornar mapa con mensaje de la excepción")
    void handleAlreadyExistsException_retornaMensaje()
    {
        AlreadyExistsException ex = new AlreadyExistsException("recurso duplicado");

        Map<String, String> result = handler.handleAlreadyExistsException(ex);

        assertEquals("recurso duplicado", result.get("message"));
    }

    @Test
    @DisplayName("BusinessException - debe retornar mapa con mensaje de la excepción")
    void handleBusinessException_retornaMensaje()
    {
        BusinessException ex = new BusinessException("regla de negocio violada");

        Map<String, String> result = handler.handleBusinessException(ex);

        assertEquals("regla de negocio violada", result.get("message"));
    }

    @Test
    @DisplayName("MethodArgumentNotValidException - debe retornar el mensaje de la excepción")
    void handleValidationException_retornaMensaje()
    {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getMessage()).thenReturn("campo requerido");

        String result = handler.handleValidationException(ex);

        assertEquals("campo requerido", result);
    }
}
