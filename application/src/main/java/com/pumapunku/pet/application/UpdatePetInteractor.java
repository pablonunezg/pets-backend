package com.pumapunku.pet.application;

import com.pumapunku.pet.domain.Pet;

/**
 * Caso de uso: actualizar los datos de una mascota existente.
 *
 * <p>Define el contrato para las implementaciones que modifican
 * los atributos de una mascota ya registrada en el sistema.</p>
 *
 * <p>Actúa como puerto de entrada (input port) en la arquitectura hexagonal.</p>
 */
public interface UpdatePetInteractor
{
    /**
     * Aplica los cambios contenidos en {@code pet} sobre la mascota correspondiente.
     *
     * @param pet entidad de dominio con el identificador y los nuevos datos; no debe ser {@code null}.
     * @throws com.pumapunku.pet.domain.exception.NotFoundException si no existe
     *         ninguna mascota con el identificador indicado.
     */
    void execute(Pet pet);
}
