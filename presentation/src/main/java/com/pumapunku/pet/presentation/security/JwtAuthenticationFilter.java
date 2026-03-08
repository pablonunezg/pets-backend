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
 * HTTP filter that intercepts each request to validate the JWT token.
 *
 * <p>Executes once per request ({@link OncePerRequestFilter}) and performs the
 * following steps:</p>
 * <ol>
 *   <li>Reads the {@code Authorization} header looking for the {@code Bearer} prefix.</li>
 *   <li>Extracts the username from the token via {@link JwtUtil}.</li>
 *   <li>If the token is valid, sets the authentication in the
 *       {@link SecurityContextHolder} so Spring Security recognizes it.</li>
 *   <li>For expired or malformed tokens, returns a JSON error response.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    /**
     * JWT utility for token extraction and validation.
     */
    private final JwtUtil jwtUtil;

    /**
     * Service for loading user details by username.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Main filter logic: validates the JWT and sets the authentication.
     *
     * @param request     incoming HTTP request.
     * @param response    HTTP response.
     * @param filterChain filter chain to continue.
     * @throws ServletException if an error occurs in the filter.
     * @throws IOException      if an I/O error occurs.
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
     * Writes an error response in JSON format.
     *
     * @param response HTTP response to write the error into.
     * @param status   HTTP status code.
     * @param message  descriptive error message.
     * @throws IOException if an error occurs while writing to the response.
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException
    {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
