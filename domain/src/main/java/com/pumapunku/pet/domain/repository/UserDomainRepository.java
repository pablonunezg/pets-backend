package com.pumapunku.pet.domain.repository;

import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.domain.filters.Filter;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (output port) del dominio para la gestión de usuarios.
 *
 * <p>Define el contrato de acceso a datos de usuarios que debe implementar
 * la capa de infraestructura. Al ser una interfaz del dominio, no tiene
 * dependencias con JPA ni con ninguna tecnología de persistencia concreta.</p>
 */
public interface UserDomainRepository
{
    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario a buscar.
     * @return un {@link Optional} con el {@link UserDomain} si se encuentra.
     */
    Optional<UserDomain> findByUserName(String username);

    /**
     * Busca usuarios que satisfagan los criterios definidos en el filtro.
     *
     * @param filter filtro de dominio con las condiciones de búsqueda; no debe ser {@code null}.
     * @return lista de usuarios que cumplen los criterios; puede estar vacía.
     */
    List<UserDomain> findByFilter(Filter filter);
}
