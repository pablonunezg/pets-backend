package com.pumapunku.pet.infrastructure.mapper;

import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.infrastructure.repository.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper MapStruct para convertir entre la entidad JPA {@link User}
 * y el modelo de dominio {@link UserDomain}.
 *
 * <p>Permite que la capa de infraestructura entregue objetos de dominio
 * a la capa de aplicación sin exponer las entidades JPA, respetando
 * el principio de inversión de dependencias.</p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper
{
    /** Instancia singleton generada por MapStruct para uso sin inyección de dependencias. */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Convierte la entidad JPA {@link User} al modelo de dominio {@link UserDomain}.
     *
     * @param user entidad JPA recuperada de la base de datos; no debe ser {@code null}.
     * @return modelo de dominio equivalente.
     */
    UserDomain toUserDomain(User user);

    /**
     * Convierte el modelo de dominio {@link UserDomain} a la entidad JPA {@link User}.
     *
     * @param userDomain modelo de dominio; no debe ser {@code null}.
     * @return entidad JPA lista para ser persistida.
     */
    User toUser(UserDomain userDomain);
}
