package com.pumapunku.pet.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Spring configuration that registers the {@code database.properties} file
 * as a property source for the application context.
 *
 * <p>By using {@link PropertySource}, all properties defined in
 * {@code database.properties} (such as DB URL, username and password)
 * become available for injection via {@code @Value} or through
 * {@code Environment} in any context bean.</p>
 */
@Configuration
@PropertySource("classpath:database.properties")
public class DatabaseProperties
{
}
