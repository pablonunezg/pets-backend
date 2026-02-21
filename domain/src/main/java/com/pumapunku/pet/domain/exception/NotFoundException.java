package com.pumapunku.pet.domain.exception;

/**
 * Excepción de dominio lanzada cuando un recurso solicitado no existe.
 *
 * <p>Corresponde a un código HTTP {@code 404 Not Found} cuando es capturada
 * por el manejador global de excepciones ({@code PetApiExceptionHandler}).</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 *   throw new NotFoundException("Pet", id.toString());
 * </pre>
 */
public class NotFoundException extends BusinessException
{
    /**
     * Crea la excepción indicando el tipo de recurso y el identificador que no fue encontrado.
     *
     * @param resource  nombre del tipo de recurso (por ejemplo, {@code "Pet"}, {@code "Refuge"}).
     * @param id        identificador del recurso no encontrado.
     */
    public NotFoundException(String resource, String id)
    {
        super(resource + " with id " + id + " not found");
    }
}
