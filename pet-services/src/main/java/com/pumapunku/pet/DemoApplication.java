package com.pumapunku.pet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point of the Spring Boot application.
 *
 * <p>Starts the Spring application context, loading all modules:
 * {@code domain}, {@code application}, {@code infrastructure}, and {@code presentation}.
 * The {@link SpringBootApplication} annotation enables component scanning,
 * auto-configuration, and configuration properties support.</p>
 */
@SpringBootApplication
public class DemoApplication
{
    /**
     * Main method that starts the application.
     *
     * @param args optional command-line arguments passed to Spring Boot.
     */
    public static void main(String[] args)
    {
        SpringApplication.run(DemoApplication.class, args);
    }
}
