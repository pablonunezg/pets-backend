package com.pumapunku.pet.infrastructure.repository.entity;

import com.pumapunku.pet.domain.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entidad JPA que representa a un usuario del sistema en la base de datos.
 *
 * <p>Mapeada a la tabla {@code app_user} (el prefijo {@code app_} evita
 * conflictos con la palabra reservada {@code USER} en algunos motores SQL).
 * Contiene las credenciales y el rol del usuario.</p>
 *
 * <p>Para exponer estos datos a la capa de dominio, se utiliza {@code UserMapper}
 * que convierte esta entidad al objeto de dominio {@code UserDomain}.</p>
 */
@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User
{
    /** Identificador único del usuario generado automáticamente por la BD. */
    @Id
    @GeneratedValue
    private UUID id;

    /** Nombre de usuario único; longitud máxima de 100 caracteres. */
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    /** Contraseña codificada con BCrypt; longitud máxima de 255 caracteres. */
    @Column(nullable = false, length = 255)
    private String password;

    /** Rol del usuario almacenado como texto en la columna {@code role}. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;
}
