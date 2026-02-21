package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.domain.filters.Filter;

import java.util.List;

/**
 * Fragmento de repositorio personalizado para búsquedas dinámicas de usuarios.
 *
 * <p>Define el contrato de búsqueda basada en filtros que extiende las capacidades
 * estándar de Spring Data JPA. La implementación concreta se encuentra en
 * {@link UserRepositoryImpl}, que Spring Data JPA detecta automáticamente
 * gracias al sufijo {@code Impl}.</p>
 *
 * <p>Este fragmento es compuesto en {@link UserRepository} junto con
 * {@link org.springframework.data.jpa.repository.JpaRepository} y
 * {@link com.pumapunku.pet.domain.repository.UserDomainRepository}.</p>
 */
public interface UserCustomRepository
{
    /**
     * Busca usuarios que satisfagan los criterios definidos en el filtro.
     *
     * @param filter filtro de dominio con las condiciones de búsqueda; no debe ser {@code null}.
     * @return lista de {@link UserDomain} que cumplen los criterios; puede estar vacía.
     */
    List<UserDomain> findByFilter(Filter filter);
}
