package com.pumapunku.pet.presentation.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Modelo de seguridad que implementa {@link UserDetails} de Spring Security.
 *
 * <p>Actúa como adaptador entre el objeto de dominio del usuario y el
 * contrato que Spring Security requiere para gestionar la autenticación
 * y la autorización.</p>
 *
 * <p>Todos los flags de estado de la cuenta ({@code isAccountNonExpired},
 * {@code isAccountNonLocked}, etc.) retornan {@code true}, lo que indica
 * que todas las cuentas están activas por defecto.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails
{
    /** Identificador único del usuario (UUID de la tabla {@code app_user}). */
    private UUID userId;

    /** Nombre de usuario único en el sistema. */
    private String username;

    /** Contraseña codificada con BCrypt. */
    private String password;

    /** Rol del usuario (por ejemplo, {@code ADMIN} o {@code USER}). */
    private String role;

    /**
     * Retorna la lista de autoridades del usuario.
     *
     * <p>Construye una {@link SimpleGrantedAuthority} con el prefijo {@code ROLE_}
     * antepuesto al rol almacenado.</p>
     *
     * @return colección con una única autoridad del tipo {@code ROLE_<rol>}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    /** @return {@code true} — la cuenta nunca expira en esta implementación. */
    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    /** @return {@code true} — la cuenta nunca se bloquea en esta implementación. */
    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    /** @return {@code true} — las credenciales nunca expiran en esta implementación. */
    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    /** @return {@code true} — el usuario siempre está habilitado en esta implementación. */
    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
