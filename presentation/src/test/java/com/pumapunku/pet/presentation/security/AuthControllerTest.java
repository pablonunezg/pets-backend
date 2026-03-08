package com.pumapunku.pet.presentation.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthController}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController")
class AuthControllerTest
{
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("login() with valid credentials should return a JWT token")
    void login_credencialesValidas_retornaToken()
    {
        UserDetails userDetails = User.builder()
                .username("alice")
                .password("hashed")
                .role("USER")
                .build();

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt.token.here");

        String token = authController.login(new AuthRequest("alice", "secret"));

        assertEquals("jwt.token.here", token);
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(userDetails);
    }

    @Test
    @DisplayName("login() with invalid credentials should propagate the AuthenticationManager exception")
    void login_credencialesInvalidas_propagaExcepcion()
    {
        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authController.login(new AuthRequest("alice", "wrong")));
    }

    @Test
    @DisplayName("AuthResponse record debe almacenar token y rol")
    void authResponse_almacenaTokenYRol()
    {
        AuthController.AuthResponse resp = new AuthController.AuthResponse("my.token", "ADMIN");
        assertEquals("my.token", resp.token());
        assertEquals("ADMIN", resp.role());
    }
}
