package com.pumapunku.pet.presentation;

import com.pumapunku.pet.domain.exception.AlreadyExistsException;
import com.pumapunku.pet.domain.exception.BusinessException;
import com.pumapunku.pet.domain.exception.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Global exception handler for the pet REST API.
 *
 * <p>Intercepts domain exceptions and transforms them into
 * HTTP responses with the appropriate status code and a standardized
 * JSON {@link ErrorResponse} body:</p>
 *
 * <pre>{@code
 * {
 *   "code":    "VALIDATION_ERROR",
 *   "message": "Data validation failed",
 *   "details": ["name: no debe estar en blanco"]
 * }
 * }</pre>
 *
 * <p>Handled exceptions:</p>
 * <ul>
 *   <li>{@link NotFoundException}               → {@code 404 Not Found}    · {@code NOT_FOUND}</li>
 *   <li>{@link AlreadyExistsException}          → {@code 409 Conflict}     · {@code ALREADY_EXISTS}</li>
 *   <li>{@link BusinessException}               → {@code 409 Conflict}     · {@code BUSINESS_ERROR}</li>
 *   <li>{@link ConstraintViolationException}    → {@code 400 Bad Request}  · {@code VALIDATION_ERROR}</li>
 *   <li>{@link DataIntegrityViolationException} → {@code 409 Conflict}     · {@code DATA_INTEGRITY_ERROR}</li>
 *   <li>{@link MethodArgumentNotValidException} → {@code 400 Bad Request}  · {@code VALIDATION_ERROR}</li>
 *   <li>{@link BadCredentialsException}         → {@code 403 Forbidden}    · {@code UNAUTHORIZED}</li>
 * </ul>
 */
@RestControllerAdvice
public class PetApiExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(PetApiExceptionHandler.class);

    // ── Error codes ──────────────────────────────────────────────────────────

    static final String CODE_NOT_FOUND = "NOT_FOUND";
    static final String CODE_ALREADY_EXISTS = "ALREADY_EXISTS";
    static final String CODE_BUSINESS_ERROR = "BUSINESS_ERROR";
    static final String CODE_VALIDATION_ERROR = "VALIDATION_ERROR";
    static final String CODE_DATA_INTEGRITY = "DATA_INTEGRITY_ERROR";
    static final String CODE_UNAUTHORIZED = "UNAUTHORIZED";

    // ── Handlers ─────────────────────────────────────────────────────────────

    /**
     * Handles resource-not-found exceptions.
     *
     * @param exception exception thrown when a resource does not exist.
     * @return {@link ErrorResponse} with {@code 404 Not Found}.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException exception)
    {
        return new ErrorResponse(CODE_NOT_FOUND, exception.getMessage());
    }

    /**
     * Handles conflict exceptions for already-existing resources.
     *
     * @param exception exception thrown when a duplicate resource is created.
     * @return {@link ErrorResponse} with {@code 409 Conflict}.
     */
    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistsException(AlreadyExistsException exception)
    {
        return new ErrorResponse(CODE_ALREADY_EXISTS, exception.getMessage());
    }

    /**
     * Handles generic business exceptions.
     *
     * @param exception business exception with a descriptive message.
     * @return {@link ErrorResponse} with {@code 409 Conflict}.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleBusinessException(BusinessException exception)
    {
        return new ErrorResponse(CODE_BUSINESS_ERROR, exception.getMessage());
    }

    /**
     * Handles Jakarta Validation constraint violations triggered by
     * {@code @Validated} in the controller (applies to {@code @RequestParam} and
     * {@code @PathVariable}).
     *
     * <p>Extracts the message from each violation and includes it in {@code details},
     * without exposing internal implementation details.</p>
     *
     * @param exception exception containing the set of constraint violations.
     * @return {@link ErrorResponse} with {@code 400 Bad Request} and list of errors.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException exception)
    {
        List<String> details = exception.getConstraintViolations().stream()
                .map(cv ->
                {
                    String field = cv.getPropertyPath().toString();
                    int dot = field.lastIndexOf('.');
                    String shortField = dot >= 0 ? field.substring(dot + 1) : field;
                    return shortField + ": " + cv.getMessage();
                })
                .sorted()
                .toList();

        return new ErrorResponse(CODE_VALIDATION_ERROR, "Data validation failed", details);
    }

    /**
     * Handles database integrity violations (unique constraints and FKs).
     *
     * <p>Extracts the constraint name from the cause chain and translates it to a
     * user-readable message, without exposing internal database details.</p>
     *
     * @param exception exception thrown by Spring when the DB rejects the operation.
     * @return {@link ErrorResponse} with {@code 409 Conflict} and a friendly message.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException exception)
    {
        String constraintInfo = extractConstraintInfo(exception);
        log.warn("DataIntegrityViolationException: {}", constraintInfo);

        String userMessage = resolveByConstraintName(constraintInfo);
        if (userMessage == null)
        {
            userMessage = resolveByViolationType(constraintInfo);
        }

        return new ErrorResponse(CODE_DATA_INTEGRITY, userMessage);
    }

    /**
     * Handles validation errors in the request body ({@code @RequestBody} / multipart).
     *
     * <p>Extracts field errors from the {@code BindingResult} and includes them in
     * {@code details} in the format {@code "field: message"}.</p>
     *
     * @param exception exception generated when a method argument fails validation.
     * @return {@link ErrorResponse} with {@code 400 Bad Request} and list of field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception)
    {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .map(f ->
                {
                    FieldError fe = exception.getBindingResult().getFieldError(f);
                    String msg = fe != null ? fe.getDefaultMessage() : "";
                    return f + ": " + msg;
                })
                .sorted()
                .toList();

        return new ErrorResponse(CODE_VALIDATION_ERROR, "Data validation failed", details);
    }

    /**
     * Handles authentication errors due to incorrect credentials.
     *
     * @param exception invalid credentials exception.
     * @return {@link ErrorResponse} with {@code 403 Forbidden}.
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleBadCredentials(BadCredentialsException exception)
    {
        return new ErrorResponse(CODE_UNAUTHORIZED, "Bad credentials");
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    /**
     * Extracts the full message from the most specific cause for use in detection.
     * This message is only used internally and in server logs — it is never returned to the client.
     */
    private String extractConstraintInfo(DataIntegrityViolationException exception)
    {
        Throwable cause = exception.getMostSpecificCause();
        return cause.getMessage() != null ? cause.getMessage().toLowerCase() : "";
    }

    /**
     * First detection pass: exact constraint names defined in
     * {@code 1.0.0_database.xml} plus the names PostgreSQL generates automatically.
     *
     * @return friendly message, or {@code null} if no match by constraint name.
     */
    private String resolveByConstraintName(String info)
    {
        if (info.contains("uk_app_user_username") || info.contains("app_user_username_key"))
        {
            return "Username is already in use.";
        }
        if (info.contains("uk_refuge_name") || info.contains("refuge_name_key"))
        {
            return "A shelter with that name already exists.";
        }
        if (info.contains("fk_pet_user"))
        {
            return "The specified user does not exist.";
        }
        if (info.contains("fk_appuser_refuge"))
        {
            return "The specified shelter does not exist.";
        }
        return null;
    }

    /**
     * Second detection pass: generic SQL keywords that describe
     * the violation type without referencing tables, columns, or real data.
     *
     * @return friendly message describing the type of problem without internal details.
     */
    private String resolveByViolationType(String info)
    {
        if (info.contains("duplicate key") || info.contains("unique constraint") || info.contains("unique index"))
        {
            return "A record with that value already exists. Please use a different one.";
        }
        if (info.contains("foreign key") || info.contains("referential integrity") || info.contains("violates fk"))
        {
            return "The referenced resource does not exist or cannot be deleted because it has associated data.";
        }
        if (info.contains("not-null") || info.contains("not null") || info.contains("null value"))
        {
            return "A required field was not provided.";
        }
        if (info.contains("check constraint") || info.contains("violates check"))
        {
            return "The submitted value does not meet the validation rules.";
        }
        return "The operation could not be completed. Please verify the submitted data and try again.";
    }
}
