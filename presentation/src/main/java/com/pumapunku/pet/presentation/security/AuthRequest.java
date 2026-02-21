package com.pumapunku.pet.presentation.security;

/**
 * DTO (Data Transfer Object) que encapsula las credenciales de autenticación.
 *
 * <p>Es recibido en el body del endpoint {@code POST /auth/login} y
 * contiene el nombre de usuario y la contraseña en texto plano que
 * serán validados por Spring Security.</p>
 *
 * @param username nombre de usuario.
 * @param password contraseña en texto plano.
 */
public record AuthRequest(String username, String password)
{
}
