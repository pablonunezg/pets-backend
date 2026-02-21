package com.pumapunku.pet.presentation.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración CORS, inyectadas desde {@code application.properties}
 * bajo el prefijo {@code cors}.
 *
 * <p>Ejemplo de configuración:</p>
 * <pre>{@code
 * cors.allowed-origin=http://midominio.local:5173
 * cors.allow-credentials=true
 * }</pre>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties
{
    /**
     * Origen permitido para las solicitudes CORS.
     * Debe incluir esquema, host y puerto, p.ej. {@code http://midominio.local:5173}.
     */
    private String allowedOrigin;

    /**
     * Indica si se permite el envío de cookies y credenciales en las solicitudes CORS.
     * Necesario para autenticación basada en cookies.
     */
    private boolean allowCredentials;
}
