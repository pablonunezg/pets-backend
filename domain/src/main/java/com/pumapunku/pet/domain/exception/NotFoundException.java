package com.pumapunku.pet.domain.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String resourceName;

    public NotFoundException(String resourceName, String id)
    {
        super(id + " not found!");
        this.id = id;
        this.resourceName = resourceName;
    }
}
