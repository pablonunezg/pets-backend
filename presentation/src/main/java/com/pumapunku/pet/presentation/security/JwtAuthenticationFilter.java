package com.pumapunku.pet.presentation.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro HTTP que intercepta cada petición para validar el token JWT.
 *
 * <p>Se ejecuta una sola vez por solicitud ({@link OncePerRequestFilter}) y realiza los
 * siguientes pasos:</p>
 * <ol>
 *   <li>Lee el encabezado {@code Authorization} buscando el prefijo {@code Bearer}.</li>
 *   <li>Extrae el nombre de usuario del token mediante {@link JwtUtil}.</li>
 *   <li>Si el token es válido, establece la autenticación en el
 *       {@link SecurityContextHolder} para que Spring Security lo reconozca.</li>
 *   <li>Ante tokens expirados o malformados, retorna una respuesta de error en JSON.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    /** Utilidad JWT para extracción y validación de tokens. */
    private final JwtUtil jwtUtil;

    /** Servicio para cargar los detalles del usuario por nombre de usuario. */
    private final UserDetailsService userDetailsService;

    /**
     * Lógica principal del filtro: valida el JWT y establece la autenticación.
     *
     * @param request     petición HTTP entrante.
     * @param response    respuesta HTTP.
     * @param filterChain cadena de filtros a continuar.
     * @throws ServletException si se produce un error en el filtro.
     * @throws IOException      si se produce un error de E/S.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try
        {
            final String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
            {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.isTokenValid(token, userDetails))
                {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        }
        catch (ExpiredJwtException e)
        {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        }
        catch (MalformedJwtException | SignatureException e)
        {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
        catch (Exception e)
        {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Escribe una respuesta de error en formato JSON.
     *
     * @param response respuesta HTTP donde se escribe el error.
     * @param status   código de estado HTTP.
     * @param message  mensaje descriptivo del error.
     * @throws IOException si ocurre un error al escribir en la respuesta.
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException
    {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
