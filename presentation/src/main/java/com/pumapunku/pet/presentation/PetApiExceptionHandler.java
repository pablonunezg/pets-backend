package com.pumapunku.pet.presentation;

import com.pumapunku.pet.domain.exception.AlreadyExistsException;
import com.pumapunku.pet.domain.exception.BusinessException;
import com.pumapunku.pet.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones de la API REST de mascotas.
 *
 * <p>Intercepta las excepciones del dominio y las transforma en respuestas
 * HTTP con el código de estado apropiado y un cuerpo JSON con el mensaje de error.
 * Al usar {@link RestControllerAdvice}, aplica a todos los controladores del módulo.</p>
 *
 * <p>Excepciones manejadas:</p>
 * <ul>
 *   <li>{@link NotFoundException}         → {@code 404 Not Found}</li>
 *   <li>{@link AlreadyExistsException}    → {@code 409 Conflict}</li>
 *   <li>{@link BusinessException}         → {@code 409 Conflict}</li>
 *   <li>{@link MethodArgumentNotValidException} → {@code 400 Bad Request}</li>
 * </ul>
 */
@RestControllerAdvice
public class PetApiExceptionHandler
{
    /**
     * Maneja excepciones de recurso no encontrado.
     *
     * @param exception excepción lanzada cuando un recurso no existe.
     * @return mapa con el mensaje de error.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException exception)
    {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("message", exception.getMessage());
        return attributes;
    }

    /**
     * Maneja excepciones de conflicto por recurso ya existente.
     *
     * @param exception excepción lanzada cuando se intenta crear un recurso duplicado.
     * @return mapa con el mensaje de error.
     */
    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleAlreadyExistsException(AlreadyExistsException exception)
    {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("message", exception.getMessage());
        return attributes;
    }

    /**
     * Maneja excepciones de negocio genéricas.
     *
     * @param exception excepción de negocio con un mensaje descriptivo.
     * @return mapa con el mensaje de error.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleBusinessException(BusinessException exception)
    {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("message", exception.getMessage());
        return attributes;
    }

    /**
     * Maneja errores de validación en el cuerpo de la petición.
     *
     * @param exception excepción generada cuando un argumento del método no pasa la validación.
     * @return mensaje de error con los detalles de validación.
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleValidationException(MethodArgumentNotValidException exception)
    {
        return exception.getMessage();
    }
}
