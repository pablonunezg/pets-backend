package com.pumapunku.pet.presentation.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CORS configuration properties, injected from {@code application.properties}
 * under the {@code cors} prefix.
 *
 * <p>Configuration example:</p>
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
     * Allowed origin for CORS requests.
     * Must include scheme, host and port, e.g. {@code http://mydomain.local:5173}.
     */
    private String allowedOrigin;

    /**
     * Indicates whether sending cookies and credentials in CORS requests is allowed.
     * Required for cookie-based authentication.
     */
    private boolean allowCredentials;
}
