package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.UpdatePetInteractor;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

/**
 * Implementación del caso de uso {@link UpdatePetInteractor}.
 *
 * <p>Delega la actualización de la mascota en el repositorio de dominio
 * {@link PetRepository}, manteniendo la capa de aplicación desacoplada
 * de los detalles de persistencia.</p>
 *
 * <p>El bean es registrado en Spring mediante {@code @Named}
 * e inyectado por constructor.</p>
 */
@Named
@RequiredArgsConstructor
public class UpdatePetInteractorImpl implements UpdatePetInteractor
{
    /** Repositorio de dominio para actualizar los datos de la mascota. */
    private final transient PetRepository petRepository;

    /**
     * {@inheritDoc}
     *
     * <p>Llama a {@link PetRepository#update(Pet)} con la entidad de dominio recibida.</p>
     */
    @Override
    public void execute(Pet pet)
    {
        petRepository.update(pet);
    }
}
