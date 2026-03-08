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
 * Spring component for creating and validating JWT tokens.
 *
 * <p>Reads the secret key and expiration time from application properties
 * ({@code jwt.secret} and {@code jwt.expiration}) and exposes methods to:
 * </p>
 * <ul>
 *   <li>Generate a signed token from user details.</li>
 *   <li>Extract the username (subject) from a token.</li>
 *   <li>Verify whether a token is valid and not expired.</li>
 * </ul>
 */
@Component
public class JwtUtil
{
    /**
     * Base64-encoded secret key used to sign JWT tokens.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token lifetime in milliseconds.
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Builds and returns the HMAC-SHA key from the configured secret.
     *
     * @return cryptographic key ready for signing or verifying tokens.
     */
    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    /**
     * Generates a signed JWT token for the given user.
     *
     * <p>The token includes the username as subject and the user's role
     * as an additional {@code role} claim.</p>
     *
     * @param userDetails authenticated user details; must not be {@code null}.
     * @return signed JWT string.
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
     * Extracts the username (subject) from the JWT token.
     *
     * @param token JWT token from which the subject is extracted.
     * @return username contained in the token.
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
     * Verifies that the token belongs to the given user and has not expired.
     *
     * @param token       JWT token to validate.
     * @param userDetails user details to compare against.
     * @return {@code true} if the token is valid; {@code false} otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks whether the token has passed its expiration date.
     *
     * @param token JWT token to check.
     * @return {@code true} if the token is expired.
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
