package com.pumapunku.pet.domain.exception;

/**
 * Domain exception thrown when a requested resource does not exist.
 *
 * <p>Maps to HTTP {@code 404 Not Found} when caught
 * by the global exception handler ({@code PetApiExceptionHandler}).</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *   throw new NotFoundException("Pet", id.toString());
 * </pre>
 */
public class NotFoundException extends BusinessException
{
    /**
     * Creates the exception indicating the resource type and the identifier that was not found.
     *
     * @param resource name of the resource type (e.g. {@code "Pet"}, {@code "Refuge"}).
     * @param id       identifier of the resource that was not found.
     */
    public NotFoundException(String resource, String id)
    {
        super(resource + " with id " + id + " not found");
    }
}
