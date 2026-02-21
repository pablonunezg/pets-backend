package com.pumapunku.pet.domain.repository;

import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;

import java.util.UUID;

/**
 * Puerto de salida (output port) del dominio para la gestión de mascotas.
 *
 * <p>Define el contrato que debe cumplir cualquier implementación de
 * persistencia de mascotas. La capa de aplicación depende únicamente
 * de esta interfaz, no de su implementación concreta en infraestructura,
 * siguiendo el principio de inversión de dependencias.</p>
 */
public interface PetRepository
{
    /**
     * Persiste una nueva mascota y la retorna con su identificador asignado.
     *
     * @param pet datos de la mascota a crear; no debe ser {@code null}.
     * @return la mascota persistida con su {@code id} generado.
     */
    Pet create(Pet pet);

    /**
     * Actualiza los datos de una mascota existente.
     *
     * @param pet entidad con el identificador y los nuevos datos; no debe ser {@code null}.
     */
    void update(Pet pet);

    /**
     * Elimina la mascota identificada por el UUID dado.
     *
     * @param id identificador único de la mascota a eliminar.
     */
    void delete(UUID id);

    /**
     * Recupera una página de mascotas registradas en el sistema.
     *
     * @param pageRequest parámetros de paginación (página 1-based y tamaño); no debe ser {@code null}.
     * @return {@link Page} con la lista de mascotas de la página solicitada y metadatos de paginación.
     */
    Page<Pet> getPets(PageRequest pageRequest);
}
