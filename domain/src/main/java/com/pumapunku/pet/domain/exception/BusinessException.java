package com.pumapunku.pet.domain.exception;

/**
 * Base exception for domain business logic errors.
 *
 * <p>Represents situations where a business rule is violated.
 * More specific subclasses (such as {@link NotFoundException} and
 * {@link AlreadyExistsException}) should be used when the error
 * context allows for it.</p>
 *
 * <p>By extending {@link RuntimeException}, it does not require callers
 * to declare it in the method signature.</p>
 */
public class BusinessException extends RuntimeException
{
    /**
     * Creates a business exception with the given descriptive message.
     *
     * @param message description of the business error.
     */
    public BusinessException(String message)
    {
        super(message);
    }
}
