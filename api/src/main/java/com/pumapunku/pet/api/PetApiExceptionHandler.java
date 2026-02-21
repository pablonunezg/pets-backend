package com.pumapunku.pet.api;

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

@RestControllerAdvice
public class PetApiExceptionHandler
{
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> exceptions(NotFoundException exception)
    {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("message", exception.getMessage());
        return attributes;
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> exceptions(AlreadyExistsException exception)
    {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("message", exception.getMessage());
        return attributes;
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> exceptions(BusinessException exception)
    {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("message", exception.getMessage());
        return attributes;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String wrongInputRequestBody(MethodArgumentNotValidException exception)
    {
        return exception.getMessage();
    }
}
