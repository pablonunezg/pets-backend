package com.pumapunku.pet.infrastructure.firestore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetCollection implements Identifier
{
    private String id;
    private String name;
}