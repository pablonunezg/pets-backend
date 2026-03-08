package com.pumapunku.pet.presentation.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link JwtAuthenticationFilter}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter")
class JwtAuthenticationFilterTest
{
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void clearContext()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("sin header Authorization debe pasar al siguiente filtro sin autenticar")
    void sinHeader_continuaFiltroSinAutenticar() throws Exception
    {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("header sin prefijo 'Bearer ' debe pasar al siguiente filtro sin autenticar")
    void headerSinBearerPrefix_continuaFiltroSinAutenticar() throws Exception
    {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("valid token should set authentication in SecurityContext")
    void tokenValido_estableceAutenticacion() throws Exception
    {
        User userDetails = User.builder()
                .username("alice")
                .password("pwd")
                .role("USER")
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
        when(jwtUtil.extractUsername("valid.token.here")).thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid("valid.token.here", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("token with null username should not set authentication")
    void tokenConUsernameNull_noEstableceAutenticacion() throws Exception
    {
        when(request.getHeader("Authorization")).thenReturn("Bearer token.sin.user");
        when(jwtUtil.extractUsername("token.sin.user")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("ExpiredJwtException debe responder 401 con mensaje 'Token expired'")
    void tokenExpirado_responde401() throws Exception
    {
        StringWriter sw = new StringWriter();
        when(request.getHeader("Authorization")).thenReturn("Bearer expired.token");
        when(jwtUtil.extractUsername(anyString())).thenThrow(mock(ExpiredJwtException.class));
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(sw.toString().contains("Token expired"));
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("MalformedJwtException debe responder 401 con mensaje 'Invalid token'")
    void tokenMalformado_responde401() throws Exception
    {
        StringWriter sw = new StringWriter();
        when(request.getHeader("Authorization")).thenReturn("Bearer bad.token");
        when(jwtUtil.extractUsername(anyString())).thenThrow(MalformedJwtException.class);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(sw.toString().contains("Invalid token"));
    }
}
