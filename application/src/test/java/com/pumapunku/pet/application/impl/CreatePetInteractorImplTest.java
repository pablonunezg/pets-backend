package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePetInteractorImplTest
{
    @InjectMocks
    private CreatePetInteractorImpl createPetInteractorImpl;

    @Mock
    private PetRepository petRepositoryMock;

    @Test
    void newCreatePetInteractorImpl()
    {
        CreatePetInteractorImpl createPetInteractorImpl = new CreatePetInteractorImpl(null);
        assertNotNull(createPetInteractorImpl);
    }

    @Test
    void newCreatePetInsteractorImpl()
    {
        Pet petParameter = new Pet("1", "Tammy");
        assertNotNull(petRepositoryMock);
        when(petRepositoryMock.create(petParameter)).thenReturn(petParameter);

        Pet pet = createPetInteractorImpl.execute(new Pet("1", "Tammy"));
        Mockito.verify(petRepositoryMock, Mockito.times(1)).create(petParameter);

        assertEquals(petParameter, pet);
    }
}
