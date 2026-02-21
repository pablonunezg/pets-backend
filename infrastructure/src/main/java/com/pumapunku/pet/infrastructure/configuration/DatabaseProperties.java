package com.pumapunku.pet.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuración de Spring que registra el archivo {@code database.properties}
 * como fuente de propiedades del contexto de aplicación.
 *
 * <p>Al usar {@link PropertySource}, todas las propiedades definidas en
 * {@code database.properties} (como URL, usuario y contraseña de la BD)
 * quedan disponibles para ser inyectadas con {@code @Value} o mediante
 * {@code Environment} en cualquier bean del contexto.</p>
 */
@Configuration
@PropertySource("classpath:database.properties")
public class DatabaseProperties
{
}
