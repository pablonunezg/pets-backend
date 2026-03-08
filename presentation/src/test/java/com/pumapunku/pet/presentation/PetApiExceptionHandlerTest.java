package com.pumapunku.pet.presentation;

import com.pumapunku.pet.domain.exception.AlreadyExistsException;
import com.pumapunku.pet.domain.exception.BusinessException;
import com.pumapunku.pet.domain.exception.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PetApiExceptionHandler}.
 *
 * <p>Verifica que todos los manejadores retornen un {@link ErrorResponse}
 * with the correct {@code code}, {@code message}, and {@code details}.</p>
 */
@DisplayName("PetApiExceptionHandler")
class PetApiExceptionHandlerTest
{
    private final PetApiExceptionHandler handler = new PetApiExceptionHandler();

    // ── NotFoundException ─────────────────────────────────────────────────

    @Test
    @DisplayName("NotFoundException → code NOT_FOUND with exception message")
    void handleNotFoundException_retornaErrorResponse()
    {
        NotFoundException ex = new NotFoundException("Pet", "abc-123");

        ErrorResponse result = handler.handleNotFoundException(ex);

        assertEquals(PetApiExceptionHandler.CODE_NOT_FOUND, result.code());
        assertEquals("Pet with id abc-123 not found", result.message());
        assertNotNull(result.details());
        assertTrue(result.details().isEmpty());
    }

    // ── AlreadyExistsException ────────────────────────────────────────────

    @Test
    @DisplayName("AlreadyExistsException → code ALREADY_EXISTS with exception message")
    void handleAlreadyExistsException_retornaErrorResponse()
    {
        AlreadyExistsException ex = new AlreadyExistsException("recurso duplicado");

        ErrorResponse result = handler.handleAlreadyExistsException(ex);

        assertEquals(PetApiExceptionHandler.CODE_ALREADY_EXISTS, result.code());
        assertEquals("recurso duplicado", result.message());
        assertTrue(result.details().isEmpty());
    }

    // ── BusinessException ─────────────────────────────────────────────────

    @Test
    @DisplayName("BusinessException → code BUSINESS_ERROR with exception message")
    void handleBusinessException_retornaErrorResponse()
    {
        BusinessException ex = new BusinessException("regla de negocio violada");

        ErrorResponse result = handler.handleBusinessException(ex);

        assertEquals(PetApiExceptionHandler.CODE_BUSINESS_ERROR, result.code());
        assertEquals("regla de negocio violada", result.message());
        assertTrue(result.details().isEmpty());
    }

    // ── BadCredentialsException ───────────────────────────────────────────

    @Test
    @DisplayName("BadCredentialsException → code UNAUTHORIZED")
    void handleBadCredentials_retornaErrorResponse()
    {
        var ex = new org.springframework.security.authentication.BadCredentialsException("bad");

        ErrorResponse result = handler.handleBadCredentials(ex);

        assertEquals(PetApiExceptionHandler.CODE_UNAUTHORIZED, result.code());
        assertEquals("Bad credentials", result.message());
    }

    // ── MethodArgumentNotValidException ──────────────────────────────────

    @Test
    @DisplayName("MethodArgumentNotValidException → code VALIDATION_ERROR con details por campo")
    void handleValidationException_retornaErrorResponseConDetails()
    {
        FieldError fieldError = new FieldError("petRequest", "name", "no debe estar en blanco");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getFieldError("name")).thenReturn(fieldError);

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ErrorResponse result = handler.handleValidationException(ex);

        assertEquals(PetApiExceptionHandler.CODE_VALIDATION_ERROR, result.code());
        assertEquals("Data validation failed", result.message());
        assertNotNull(result.details());
        assertEquals(1, result.details().size());
        assertTrue(result.details().getFirst().startsWith("name:"));
    }

    @Test
    @DisplayName("MethodArgumentNotValidException with no errors → empty details")
    void handleValidationException_sinErrores_detailsVacio()
    {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ErrorResponse result = handler.handleValidationException(ex);

        assertEquals(PetApiExceptionHandler.CODE_VALIDATION_ERROR, result.code());
        assertTrue(result.details().isEmpty());
    }

    // ── ConstraintViolationException ──────────────────────────────────────

    @Nested
    @DisplayName("ConstraintViolationException (@Validated en @RequestParam)")
    class ConstraintViolationTests
    {
        @Test
        @DisplayName("debe retornar VALIDATION_ERROR con details por campo")
        void violation_retornaErrorResponseConDetails()
        {
            ConstraintViolation<?> v1 = buildViolation("createPet.petRequest.name", "no debe estar en blanco");
            ConstraintViolation<?> v2 = buildViolation("createPet.petRequest.description", "no debe estar en blanco");
            ConstraintViolationException ex = new ConstraintViolationException(Set.of(v1, v2));

            ErrorResponse result = handler.handleConstraintViolation(ex);

            assertEquals(PetApiExceptionHandler.CODE_VALIDATION_ERROR, result.code());
            assertEquals("Data validation failed", result.message());
            assertNotNull(result.details());
            assertEquals(2, result.details().size());
            assertTrue(result.details().stream().anyMatch(e -> e.startsWith("name:")));
            assertTrue(result.details().stream().anyMatch(e -> e.startsWith("description:")));
        }

        @Test
        @DisplayName("no violations → empty details")
        void sinViolaciones_detailsVacio()
        {
            ConstraintViolationException ex = new ConstraintViolationException(Set.of());

            ErrorResponse result = handler.handleConstraintViolation(ex);

            assertEquals(PetApiExceptionHandler.CODE_VALIDATION_ERROR, result.code());
            assertNotNull(result.details());
            assertTrue(result.details().isEmpty());
        }

        private ConstraintViolation<?> buildViolation(String propertyPath, String message)
        {
            ConstraintViolation<?> cv = mock(ConstraintViolation.class);
            Path path = mock(Path.class);
            when(path.toString()).thenReturn(propertyPath);
            when(cv.getPropertyPath()).thenReturn(path);
            when(cv.getMessage()).thenReturn(message);
            return cv;
        }
    }

    // ── DataIntegrityViolationException ───────────────────────────────────

    @Nested
    @DisplayName("DataIntegrityViolationException")
    class DataIntegrityTests
    {
        @Test
        @DisplayName("todos los handlers retornan code DATA_INTEGRITY_ERROR")
        void todosLosHandlers_retornanCodeDataIntegrity()
        {
            var ex = causeWith("duplicate key value violates unique constraint \"uk_app_user_username\"");
            assertEquals(PetApiExceptionHandler.CODE_DATA_INTEGRITY, handler.handleDataIntegrityViolation(ex).code());
        }

        // ── By constraint name ──────────────────────────────────────

        @Test
        @DisplayName("constraint uk_app_user_username → mensaje de usuario duplicado")
        void username_constraintViolation_retornaMensajeAmigable()
        {
            var ex = causeWith("duplicate key value violates unique constraint \"uk_app_user_username\"");
            assertEquals("Username is already in use.", message(ex));
        }

        @Test
        @DisplayName("constraint postgres app_user_username_key → mensaje de usuario duplicado")
        void username_postgresKey_retornaMensajeAmigable()
        {
            var ex = causeWith("duplicate key value violates unique constraint \"app_user_username_key\"");
            assertEquals("Username is already in use.", message(ex));
        }

        @Test
        @DisplayName("constraint uk_refuge_name → mensaje de refugio duplicado")
        void refugeName_constraintViolation_retornaMensajeAmigable()
        {
            var ex = causeWith("duplicate key value violates unique constraint \"uk_refuge_name\"");
            assertEquals("Ya existe un refugio con ese nombre.", message(ex));
        }

        @Test
        @DisplayName("constraint fk_pet_user → mensaje de usuario no encontrado")
        void fkPetUser_retornaMensajeAmigable()
        {
            var ex = causeWith("insert on table \"pet\" violates foreign key constraint \"fk_pet_user\"");
            assertEquals("El usuario especificado no existe.", message(ex));
        }

        @Test
        @DisplayName("constraint fk_appuser_refuge → mensaje de refugio no encontrado")
        void fkAppUserRefuge_retornaMensajeAmigable()
        {
            var ex = causeWith("insert on table \"app_user\" violates foreign key constraint \"fk_appuser_refuge\"");
            assertEquals("El refugio especificado no existe.", message(ex));
        }

        // ── By violation type (second pass) ──────────────────────────

        @Test
        @DisplayName("'duplicate key' with unknown constraint → generic duplicate message")
        void duplicateKey_sinConstraintConocido_retornaMensajeDuplicado()
        {
            var ex = causeWith("duplicate key value violates unique constraint \"some_other_unique_idx\"");
            String msg = message(ex);
            assertTrue(msg.contains("ya existe") || msg.contains("diferente"),
                    "Debe indicar que el valor ya existe");
        }

        @Test
        @DisplayName("'foreign key' with unknown constraint → generic reference message")
        void foreignKey_sinConstraintConocido_retornaMensajeReferencia()
        {
            var ex = causeWith("violates foreign key constraint \"some_fk_xyz\"");
            String msg = message(ex);
            assertTrue(msg.contains("referenciado") || msg.contains("asociados"),
                    "Debe indicar problema de referencia");
        }

        @Test
        @DisplayName("'not-null' → mensaje de campo obligatorio")
        void notNull_retornaMensajeCampoObligatorio()
        {
            var ex = causeWith("null value in column violates not-null constraint");
            String msg = message(ex);
            assertTrue(msg.contains("obligatorio") || msg.contains("no fue enviado"),
                    "Debe indicar que falta un campo");
        }

        // ── Security: no internal data leaked ───────────────────────

        @Test
        @DisplayName("la respuesta nunca debe contener detalles internos de la BD")
        void respuesta_noContieneDetallesInternos()
        {
            var ex = causeWith("org.postgresql.util.PSQLException: ERROR: duplicate key value "
                    + "violates unique constraint \"uk_app_user_username\" "
                    + "Detail: Key (username)=(admin) already exists.");

            String msg = message(ex);
            assertFalse(msg.contains("PSQLException"));
            assertFalse(msg.contains("Detail:"));
            assertFalse(msg.contains("(admin)"));
        }

        @Test
        @DisplayName("unknown constraint without SQL keywords → final generic message")
        void constraintDesconocido_sinPalabrasClave_retornaMensajeFinal()
        {
            var ex = causeWith("something completely unrecognized from db internals");
            String msg = message(ex);
            assertNotNull(msg);
            assertFalse(msg.isEmpty());
            assertFalse(msg.toLowerCase().contains("sql"));
            assertFalse(msg.toLowerCase().contains("constraint"));
        }

        // ── Helpers ───────────────────────────────────────────────────────

        private DataIntegrityViolationException causeWith(String causeMessage)
        {
            return new DataIntegrityViolationException("error", new RuntimeException(causeMessage));
        }

        private String message(DataIntegrityViolationException ex)
        {
            return handler.handleDataIntegrityViolation(ex).message();
        }
    }
}
