package com.pumapunku.pet.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidad de dominio que representa a un usuario del sistema.
 *
 * <p>Contiene los datos mínimos necesarios para autenticación y autorización.
 * Al pertenecer al dominio, no tiene dependencias con JPA, Spring Security
 * ni ninguna otra librería de infraestructura.</p>
 *
 * <p>La capa de infraestructura mapea esta clase desde/hacia la entidad JPA
 * {@code User} usando {@code UserMapper}.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDomain
{
    /** Identificador único del usuario. */
    private UUID id;

    /** Nombre de usuario único utilizado para la autenticación. */
    private String username;

    /** Contraseña codificada con BCrypt. */
    private String password;

    /** Rol que determina los permisos del usuario en el sistema. */
    private Role role;
}
