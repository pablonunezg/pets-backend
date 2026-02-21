package com.pumapunku.pet.application;

import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;

/**
 * Caso de uso: obtener el listado paginado de mascotas.
 *
 * <p>Define el contrato que deben cumplir las implementaciones encargadas
 * de recuperar las mascotas registradas en el sistema de forma paginada.</p>
 *
 * <p>Pertenece a la capa de aplicación siguiendo los principios de
 * Clean Architecture, actuando como puerto de entrada (input port).</p>
 */
public interface GetPetsInteractor
{
    /**
     * Ejecuta el caso de uso y devuelve una página de mascotas.
     *
     * <p>Si no se requiere paginación explícita, use
     * {@link PageRequest#ofDefaults()} para obtener los valores por defecto
     * (página 1, tamaño 200).</p>
     *
     * @param pageRequest parámetros de paginación (página 1-based y tamaño); no debe ser {@code null}.
     * @return {@link Page} con las mascotas de la página solicitada; nunca {@code null}.
     */
    Page<Pet> execute(PageRequest pageRequest);
}
