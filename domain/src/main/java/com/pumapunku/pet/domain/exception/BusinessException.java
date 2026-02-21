package com.pumapunku.pet.domain.exception;

/**
 * Excepción base para errores de lógica de negocio del dominio.
 *
 * <p>Representa situaciones en las que se viola una regla de negocio.
 * Las subclases más específicas (como {@link NotFoundException} y
 * {@link AlreadyExistsException}) deben usarse cuando el contexto
 * del error lo permita.</p>
 *
 * <p>Al extender {@link RuntimeException}, no obliga al código que la lanza
 * a declararla en la firma del método.</p>
 */
public class BusinessException extends RuntimeException
{
    /**
     * Crea una excepción de negocio con el mensaje descriptivo indicado.
     *
     * @param message descripción del error de negocio.
     */
    public BusinessException(String message)
    {
        super(message);
    }
}
