package com.pumapunku.pet.presentation.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Utilidad de Spring para la creación y validación de tokens JWT.
 *
 * <p>Lee la clave secreta y el tiempo de expiración desde las propiedades
 * de la aplicación ({@code jwt.secret} y {@code jwt.expiration}) y expone
 * métodos para:</p>
 * <ul>
 *   <li>Generar un token firmado a partir de los detalles del usuario.</li>
 *   <li>Extraer el nombre de usuario (subject) de un token.</li>
 *   <li>Verificar si un token es válido y no ha expirado.</li>
 * </ul>
 */
@Component
public class JwtUtil
{
    /** Clave secreta en Base64 utilizada para firmar los tokens JWT. */
    @Value("${jwt.secret}")
    private String secret;

    /** Tiempo de vida del token en milisegundos. */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Construye y retorna la clave HMAC-SHA a partir del secreto configurado.
     *
     * @return clave criptográfica lista para firmar o verificar tokens.
     */
    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    /**
     * Genera un token JWT firmado para el usuario indicado.
     *
     * <p>El token incluye el nombre de usuario como subject y el rol
     * del usuario como claim adicional {@code role}.</p>
     *
     * @param userDetails detalles del usuario autenticado; no debe ser {@code null}.
     * @return cadena JWT firmada.
     */
    public String generateToken(UserDetails userDetails)
    {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(t -> t.replace("ROLE_", ""))
                        .collect(Collectors.joining(",")))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae el nombre de usuario (subject) del token JWT.
     *
     * @param token token JWT del que se extrae el subject.
     * @return nombre de usuario contenido en el token.
     */
    public String extractUsername(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Verifica que el token pertenece al usuario indicado y no ha expirado.
     *
     * @param token       token JWT a validar.
     * @param userDetails detalles del usuario con quien comparar.
     * @return {@code true} si el token es válido; {@code false} en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Comprueba si el token ha superado su fecha de expiración.
     *
     * @param token token JWT a verificar.
     * @return {@code true} si el token está expirado.
     */
    private boolean isTokenExpired(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }
}
