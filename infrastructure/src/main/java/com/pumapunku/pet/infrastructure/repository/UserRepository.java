package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.domain.repository.UserDomainRepository;
import com.pumapunku.pet.infrastructure.mapper.UserMapper;
import com.pumapunku.pet.infrastructure.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio principal de usuarios que combina múltiples capacidades.
 *
 * <p>Extiende:</p>
 * <ul>
 *   <li>{@link JpaRepository} — operaciones CRUD estándar de Spring Data JPA.</li>
 *   <li>{@link JpaSpecificationExecutor} — soporte para consultas con Specification.</li>
 *   <li>{@link UserCustomRepository} — búsquedas dinámicas basadas en filtros de dominio.</li>
 *   <li>{@link UserDomainRepository} — puerto de salida requerido por el dominio.</li>
 * </ul>
 *
 * <p>El método {@link #findByUserName(String)} adapta la respuesta JPA al modelo
 * de dominio mediante {@link UserMapper}, manteniendo el dominio libre de
 * dependencias de infraestructura.</p>
 */
public interface UserRepository extends JpaRepository<User, UUID>,
        JpaSpecificationExecutor<User>,
        UserCustomRepository,
        UserDomainRepository
{
    /**
     * Busca un usuario por su nombre de usuario a nivel de entidad JPA.
     *
     * @param username nombre de usuario a buscar.
     * @return un {@link Optional} con la entidad {@link User} si existe.
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su nombre de usuario y lo convierte al modelo de dominio.
     *
     * <p>Implementa el método requerido por {@link UserDomainRepository}.</p>
     *
     * @param username nombre de usuario a buscar.
     * @return un {@link Optional} con el {@link UserDomain} correspondiente, si existe.
     */
    default Optional<UserDomain> findByUserName(String username)
    {
        return findByUsername(username).map(UserMapper.INSTANCE::toUserDomain);
    }
}
