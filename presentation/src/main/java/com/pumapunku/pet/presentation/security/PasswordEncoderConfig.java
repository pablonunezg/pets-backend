package com.pumapunku.pet.presentation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring configuration that exposes the {@link PasswordEncoder} bean.
 *
 * <p>Declares a {@link BCryptPasswordEncoder} as an available context bean,
 * so it can be injected into any component that needs to
 * encode or verify passwords (e.g. {@link AuthController}).</p>
 *
 * <p>BCrypt applies a default cost factor (10 rounds) that makes
 * brute-force attacks computationally expensive.</p>
 */
@Configuration
public class PasswordEncoderConfig
{
    /**
     * Creates and registers the BCrypt password encoder.
     *
     * @return {@link BCryptPasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
