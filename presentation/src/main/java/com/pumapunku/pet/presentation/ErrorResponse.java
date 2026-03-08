package com.pumapunku.pet.presentation;

import java.util.List;

/**
 * Standard error response DTO for all REST API errors.
 *
 * <p>All exceptions handled by {@link PetApiExceptionHandler}
 * return this object as a JSON body, guaranteeing a uniform contract
 * for API consumers:</p>
 *
 * <pre>{@code
 * {
 *   "code":    "VALIDATION_ERROR",
 *   "message": "Data validation failed",
 *   "details": ["name: no debe estar en blanco"]
 * }
 * }</pre>
 *
 * @param code    error code in SNAKE_UPPER_CASE identifying the type of problem.
 * @param message human-readable error description aimed at API consumers.
 * @param details optional list of additional details (invalid fields, etc.).
 *                May be {@code null} or empty when there is no additional information.
 */
public record ErrorResponse(String code, String message, List<String> details)
{
    /**
     * Convenience constructor for errors without additional details.
     *
     * @param code    error code.
     * @param message error message.
     */
    public ErrorResponse(String code, String message)
    {
        this(code, message, List.of());
    }
}
