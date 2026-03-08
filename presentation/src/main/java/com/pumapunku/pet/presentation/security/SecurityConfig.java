package com.pumapunku.pet.presentation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central Spring Security configuration for the application.
 *
 * <p>Defines HTTP access rules, CORS, and registers the JWT filter in the
 * security filter chain. Design decisions:</p>
 * <ul>
 *   <li>CSRF disabled because the API is stateless (JWT).</li>
 *   <li>The {@code /auth/**} endpoint is public; all other routes require authentication.</li>
 *   <li>HTTP sessions disabled ({@link SessionCreationPolicy#STATELESS}).</li>
 *   <li>{@link JwtAuthenticationFilter} is inserted before the standard username/password filter.</li>
 *   <li>CORS configured for the React frontend specified in {@code application.properties}.</li>
 *   <li>{@code allowCredentials=true} to support cookie-based authentication.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
    /**
     * JWT filter that validates the token on each incoming request.
     */
    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * CORS properties read from {@code application.properties}.
     */
    private final CorsProperties corsProperties;

    /**
     * Configures and builds the HTTP security filter chain.
     *
     * <p>Integrates CORS using the {@link CorsConfigurationSource} defined in
     * {@link #corsConfigurationSource()}, so origin rules are applied
     * before any other security filter.</p>
     *
     * @param http HTTP security builder provided by Spring.
     * @return the configured filter chain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Configures CORS rules for all API routes.
     *
     * <p>The allowed origin and credentials policy are read from
     * {@link CorsProperties} ({@code cors.allowed-origin} and {@code cors.allow-credentials}
     * in {@code application.properties}).</p>
     *
     * <p>The {@code X-Total-Count} header is exposed so the frontend can
     * read it in paginated responses.</p>
     *
     * @return CORS configuration source registered for all routes ({@code /**}).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(corsProperties.getAllowedOrigin()));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("X-Total-Count"));
        config.setAllowCredentials(corsProperties.isAllowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Exposes the {@link AuthenticationManager} as a Spring bean,
     * required so {@link AuthController} can authenticate credentials.
     *
     * @param config authentication configuration from the context.
     * @return the configured authentication manager.
     * @throws Exception if an error occurs obtaining the manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception
    {
        return config.getAuthenticationManager();
    }
}
