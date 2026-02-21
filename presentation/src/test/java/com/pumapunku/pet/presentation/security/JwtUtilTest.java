package com.pumapunku.pet.presentation.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link JwtUtil}.
 */
@DisplayName("JwtUtil")
class JwtUtilTest
{
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp()
    {
        jwtUtil = new JwtUtil();
        // Generar una clave HMAC-SHA256 válida para los tests
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String secret = Encoders.BASE64.encode(key.getEncoded());
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3_600_000L); // 1 hora
    }

    private UserDetails mockUser(String username)
    {
        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn(username);
        when(ud.getAuthorities()).thenReturn(
                (Collection) List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        return ud;
    }

    @Test
    @DisplayName("generateToken() debe retornar un token no nulo y no vacío")
    void generateToken_retornaTokenNoVacio()
    {
        String token = jwtUtil.generateToken(mockUser("alice"));
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("extractUsername() debe retornar el username del token generado")
    void extractUsername_retornaUsernameCorrect()
    {
        UserDetails user = mockUser("alice");
        String token = jwtUtil.generateToken(user);

        assertEquals("alice", jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("isTokenValid() debe retornar true para token recién generado")
    void isTokenValid_tokenFresco_retornaTrue()
    {
        UserDetails user = mockUser("alice");
        String token = jwtUtil.generateToken(user);

        assertTrue(jwtUtil.isTokenValid(token, user));
    }

    @Test
    @DisplayName("isTokenValid() debe retornar false si el username no coincide")
    void isTokenValid_usernameDiferente_retornaFalse()
    {
        UserDetails alice = mockUser("alice");
        String token = jwtUtil.generateToken(alice);

        UserDetails bob = mockUser("bob");
        assertFalse(jwtUtil.isTokenValid(token, bob));
    }

    @Test
    @DisplayName("isTokenValid() debe retornar false para token expirado")
    void isTokenValid_tokenExpirado_retornaFalse()
    {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1L);
        UserDetails user = mockUser("alice");
        String token = jwtUtil.generateToken(user);

        assertThrows(ExpiredJwtException.class, () -> jwtUtil.isTokenValid(token, user));
    }

    @Test
    @DisplayName("generateToken() debe incluir el claim 'role' sin prefijo ROLE_")
    void generateToken_incluyeRolSinPrefijo()
    {
        UserDetails user = mockUser("alice");
        String token = jwtUtil.generateToken(user);
        // El token tiene 3 partes: header.payload.signature
        String payload = new String(java.util.Base64.getUrlDecoder().decode(
                token.split("\\.")[1]
        ));
        assertTrue(payload.contains("USER"));
    }
}
