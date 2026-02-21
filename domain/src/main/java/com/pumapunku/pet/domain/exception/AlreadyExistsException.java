package com.pumapunku.pet.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta crear un recurso que ya existe.
 *
 * <p>Corresponde a un código HTTP {@code 409 Conflict} cuando es capturada
 * por el manejador global de excepciones ({@code PetApiExceptionHandler}).</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 *   throw new AlreadyExistsException("Ya existe una mascota con ese nombre");
 * </pre>
 */
public class AlreadyExistsException extends BusinessException
{
    /**
     * Crea la excepción con el mensaje descriptivo del conflicto.
     *
     * @param message descripción del recurso que ya existe.
     */
    public AlreadyExistsException(String message)
    {
        super(message);
    }
}
