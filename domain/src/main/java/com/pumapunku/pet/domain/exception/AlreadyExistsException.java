package com.pumapunku.pet.domain.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlreadyExistsException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private String id;
    private String collectionName;

    public AlreadyExistsException(String collectionName, String id)
    {
        super(String.format("%s id already exists in the table %s", id, collectionName));
        this.id = id;
        this.collectionName = collectionName;
    }
}