package com.pumapunku.pet.presentation.converter;

import com.pumapunku.pet.presentation.request.PetRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Convertidor de Spring MVC que transforma una cadena JSON en un objeto {@link PetRequest}.
 *
 * <p>Es necesario cuando el endpoint recibe una petición multipart y el campo
 * {@code pet} llega como texto JSON (no como JSON estructurado en el body).
 * Spring MVC registra automáticamente este convertidor al detectar la anotación
 * {@link Component} junto con la implementación de {@link Converter}.</p>
 *
 * <p>Ejemplo de uso en el controlador:</p>
 * <pre>
 *   {@code @RequestParam("pet") @Valid PetRequest petRequest}
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class PetConverter implements Converter<String, PetRequest>
{
    /** Mapper de Jackson utilizado para deserializar el JSON recibido. */
    private final ObjectMapper objectMapper;

    /**
     * Convierte la cadena JSON recibida en un objeto {@link PetRequest}.
     *
     * @param source cadena con el JSON que representa la petición de mascota.
     * @return objeto {@link PetRequest} deserializado.
     * @throws IllegalArgumentException si el JSON es inválido o no puede ser parseado.
     */
    @Override
    public PetRequest convert(String source)
    {
        try
        {
            return objectMapper.readValue(source, PetRequest.class);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("JSON inválido para Pet: " + e.getMessage());
        }
    }
}
