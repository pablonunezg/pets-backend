package com.pumapunku.pet.domain.exception;

/**
 * Domain exception thrown when attempting to create a resource that already exists.
 *
 * <p>Maps to HTTP {@code 409 Conflict} when caught
 * by the global exception handler ({@code PetApiExceptionHandler}).</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *   throw new AlreadyExistsException("A pet with that name already exists");
 * </pre>
 */
public class AlreadyExistsException extends BusinessException
{
    /**
     * Creates the exception with a descriptive message about the conflict.
     *
     * @param message description of the resource that already exists.
     */
    public AlreadyExistsException(String message)
    {
        super(message);
    }
}
