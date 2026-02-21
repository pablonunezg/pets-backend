package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.GetPetsInteractor;
import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

/**
 * Implementación del caso de uso {@link GetPetsInteractor}.
 *
 * <p>Delega la consulta paginada de mascotas al repositorio del dominio
 * {@link PetRepository}, manteniendo la lógica de aplicación
 * separada de los detalles de persistencia.</p>
 *
 * <p>Es registrada como bean de Spring mediante {@code @Named} y
 * recibe sus dependencias por constructor gracias a {@code @RequiredArgsConstructor}.</p>
 */
@Named
@RequiredArgsConstructor
public class GetPetsInteractorImpl implements GetPetsInteractor
{
    /** Repositorio de dominio para acceder a las mascotas registradas. */
    private final transient PetRepository petRepository;

    /**
     * {@inheritDoc}
     *
     * <p>Recupera la página de mascotas llamando a
     * {@link PetRepository#getPets(PageRequest)}.</p>
     */
    @Override
    public Page<Pet> execute(PageRequest pageRequest)
    {
        return petRepository.getPets(pageRequest);
    }
}
