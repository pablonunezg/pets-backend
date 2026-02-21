package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.domain.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DeletePetInteractorImplTest
{
    @InjectMocks
    private DeletePetInteractorImpl deletePetInteractorImpl;

    @Mock
    private PetRepository petRepositoryMock;

    @Test
    void newCreatePetInteractorImpl()
    {
        assertNotNull(deletePetInteractorImpl);
    }

    @Test
    void newCreatePetInsteractorImpl()
    {
        assertNotNull(petRepositoryMock);
        deletePetInteractorImpl.execute("1");
        Mockito.verify(petRepositoryMock, Mockito.times(1)).delete("1");
    }
}
