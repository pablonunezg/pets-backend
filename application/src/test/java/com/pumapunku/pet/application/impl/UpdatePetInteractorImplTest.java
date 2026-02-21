package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UpdatePetInteractorImplTest
{
    @InjectMocks
    UpdatePetInteractorImpl updatePetInteractorImpl;

    @Mock
    private PetRepository petRepositoryMock;

    @Test
    void newCreatePetInteractorImpl()
    {
        UpdatePetInteractorImpl updatePetInteractorImpl = new UpdatePetInteractorImpl(null);
        assertNotNull(updatePetInteractorImpl);
    }

    @Test
    void newCreatePetInsteractorImpl()
    {
        Pet petParameter = new Pet("1", "Tammy");
        assertNotNull(petRepositoryMock);

        updatePetInteractorImpl.execute(new Pet("1", "Tammy"));
        Mockito.verify(petRepositoryMock, Mockito.times(1)).update(petParameter);
    }
}
