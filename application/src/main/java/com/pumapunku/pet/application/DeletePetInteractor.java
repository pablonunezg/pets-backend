package com.pumapunku.pet.application;

import java.util.UUID;

/**
 * Caso de uso: eliminar una mascota del sistema.
 *
 * <p>Define el contrato para las implementaciones que se encargan
 * de remover permanentemente una mascota identificada por su UUID.</p>
 *
 * <p>Actúa como puerto de entrada (input port) en la arquitectura hexagonal.</p>
 */
public interface DeletePetInteractor
{
    /**
     * Elimina la mascota asociada al identificador recibido.
     *
     * @param petId identificador único de la mascota a eliminar; no debe ser {@code null}.
     * @throws com.pumapunku.pet.domain.exception.NotFoundException si no existe
     *         ninguna mascota con ese identificador.
     */
    void execute(UUID petId);
}
