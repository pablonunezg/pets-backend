package com.pumapunku.pet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada principal de la aplicación Spring Boot.
 *
 * <p>Inicia el contexto de aplicación de Spring, que carga todos los módulos:
 * {@code domain}, {@code application}, {@code infrastructure} y {@code presentation}.
 * La anotación {@link SpringBootApplication} activa el escaneo de componentes,
 * la autoconfiguración y el soporte de propiedades de configuración.</p>
 */
@SpringBootApplication
public class DemoApplication
{
    /**
     * Método principal que arranca la aplicación.
     *
     * @param args argumentos de línea de comandos opcionales pasados a Spring Boot.
     */
    public static void main(String[] args)
    {
        SpringApplication.run(DemoApplication.class, args);
    }
}
