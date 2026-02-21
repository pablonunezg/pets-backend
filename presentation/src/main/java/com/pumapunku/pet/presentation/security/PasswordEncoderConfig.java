package com.pumapunku.pet.presentation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de Spring que expone el bean {@link PasswordEncoder}.
 *
 * <p>Declara un {@link BCryptPasswordEncoder} como bean disponible en el contexto,
 * de manera que pueda ser inyectado en cualquier componente que necesite
 * codificar o verificar contraseñas (por ejemplo, {@link AuthController}).</p>
 *
 * <p>BCrypt aplica un factor de costo por defecto (10 rondas) que hace
 * computacionalmente costoso el ataque por fuerza bruta.</p>
 */
@Configuration
public class PasswordEncoderConfig
{
    /**
     * Crea y registra el codificador de contraseñas BCrypt.
     *
     * @return instancia de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
