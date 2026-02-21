package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.DeletePetInteractor;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Implementación del caso de uso {@link DeletePetInteractor}.
 *
 * <p>Delega la eliminación de la mascota en el repositorio de dominio
 * {@link PetRepository}, manteniendo la lógica de aplicación
 * libre de dependencias de infraestructura.</p>
 *
 * <p>El bean es registrado en Spring mediante {@code @Named}
 * e inyectado por constructor.</p>
 */
@Named
@RequiredArgsConstructor
public class DeletePetInteractorImpl implements DeletePetInteractor
{
    /** Repositorio de dominio para eliminar la mascota por identificador. */
    private final transient PetRepository petRepository;

    /**
     * {@inheritDoc}
     *
     * <p>Llama a {@link PetRepository#delete(UUID)} con el identificador recibido.</p>
     */
    @Override
    public void execute(UUID id)
    {
        petRepository.delete(id);
    }
}
