package com.pumapunku.pet.presentation.converter;

import com.pumapunku.pet.presentation.request.PetRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Spring MVC converter that transforms a JSON string into a {@link PetRequest} object.
 *
 * <p>Required when the endpoint receives a multipart request and the
 * {@code pet} field arrives as JSON text (not as structured JSON in the body).
 * Spring MVC automatically registers this converter when it detects the
 * {@link Component} annotation together with the {@link Converter} implementation.</p>
 *
 * <p>Usage example in the controller:</p>
 * <pre>
 *   {@code @RequestParam("pet") @Valid PetRequest petRequest}
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class PetConverter implements Converter<String, PetRequest>
{
    /**
     * Jackson mapper used to deserialize the received JSON.
     */
    private final ObjectMapper objectMapper;

    /**
     * Converts the received JSON string into a {@link PetRequest} object.
     *
     * @param source string containing the JSON representing the pet request.
     * @return deserialized {@link PetRequest} object.
     * @throws IllegalArgumentException if the JSON is invalid or cannot be parsed.
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
            throw new IllegalArgumentException("Invalid JSON for Pet: " + e.getMessage());
        }
    }
}
