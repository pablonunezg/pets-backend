package com.pumapunku.pet.presentation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado de la autenticación de usuarios.
 *
 * <p>Expone el endpoint {@code POST /auth/login} que recibe las credenciales
 * del usuario, las valida contra el {@link AuthenticationManager} de Spring Security
 * y devuelve un token JWT firmado si la autenticación es exitosa.</p>
 *
 * <p>Pertenece a la capa de presentación, dentro del sub-paquete de seguridad.</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController
{
    /** Gestor de autenticación de Spring Security. */
    private final AuthenticationManager authManager;

    /** Servicio para cargar los detalles del usuario a partir del nombre de usuario. */
    private final UserDetailsService userDetailsService;

    /** Utilidad para la generación y validación de tokens JWT. */
    private final JwtUtil jwtUtil;

    /** Codificador de contraseñas utilizado para comparar credenciales. */
    private final PasswordEncoder passwordEncoder;

    /**
     * Autentica al usuario con las credenciales recibidas y retorna un token JWT.
     *
     * @param request objeto con el nombre de usuario y contraseña.
     * @return token JWT como cadena de texto.
     */
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request)
    {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        return jwtUtil.generateToken(userDetails);
    }

    /**
     * Registro interno para la respuesta de autenticación.
     *
     * @param token  token JWT generado.
     * @param role   rol del usuario autenticado.
     */
    public record AuthResponse(
            String token,
            String role
    )
    {
    }
}
