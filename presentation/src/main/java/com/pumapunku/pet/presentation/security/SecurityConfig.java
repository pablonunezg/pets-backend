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
 * Configuración central de Spring Security para la aplicación.
 *
 * <p>Define las reglas de acceso HTTP, CORS y registra el filtro JWT en la cadena
 * de filtros de seguridad. Las decisiones de diseño son:</p>
 * <ul>
 *   <li>CSRF deshabilitado, ya que la API es stateless (JWT).</li>
 *   <li>El endpoint {@code /auth/**} es público; cualquier otra ruta requiere autenticación.</li>
 *   <li>Sesiones HTTP deshabilitadas ({@link SessionCreationPolicy#STATELESS}).</li>
 *   <li>{@link JwtAuthenticationFilter} se inserta antes del filtro estándar de usuario/contraseña.</li>
 *   <li>CORS configurado para el frontend React especificado en {@code application.properties}.</li>
 *   <li>{@code allowCredentials=true} para soportar autenticación con cookies.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
    /** Filtro JWT que valida el token en cada solicitud entrante. */
    private final JwtAuthenticationFilter jwtAuthFilter;

    /** Propiedades CORS leídas desde {@code application.properties}. */
    private final CorsProperties corsProperties;

    /**
     * Configura y construye la cadena de filtros de seguridad HTTP.
     *
     * <p>Integra CORS usando el {@link CorsConfigurationSource} definido en
     * {@link #corsConfigurationSource()}, de modo que las reglas de origen se
     * aplican antes que cualquier otro filtro de seguridad.</p>
     *
     * @param http constructor del contexto de seguridad proporcionado por Spring.
     * @return la cadena de filtros configurada.
     * @throws Exception si ocurre un error durante la configuración.
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
     * Configura las reglas CORS para todas las rutas de la API.
     *
     * <p>El origen permitido y la política de credenciales se leen desde
     * {@link CorsProperties} ({@code cors.allowed-origin} y {@code cors.allow-credentials}
     * en {@code application.properties}).</p>
     *
     * <p>Se expone el header {@code X-Total-Count} para que el frontend pueda
     * leerlo en respuestas paginadas.</p>
     *
     * @return fuente de configuración CORS registrada en todas las rutas ({@code /**}).
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
     * Expone el {@link AuthenticationManager} como bean de Spring,
     * necesario para que {@link AuthController} pueda autenticar credenciales.
     *
     * @param config configuración de autenticación del contexto.
     * @return el gestor de autenticación configurado.
     * @throws Exception si ocurre un error al obtener el gestor.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception
    {
        return config.getAuthenticationManager();
    }
}
