package com.pumapunku.pet.presentation.converter;

import com.pumapunku.pet.presentation.request.PetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link PetConverter}.
 */
@DisplayName("PetConverter")
class PetConverterTest
{
    private PetConverter converter;

    @BeforeEach
    void setUp()
    {
        converter = new PetConverter(new ObjectMapper());
    }

    @Test
    @DisplayName("convert() debe deserializar JSON válido a PetRequest")
    void convert_jsonValido_retornaPetRequest() throws Exception
    {
        UUID refugeId = UUID.randomUUID();
        String json = """
                {
                  "name": "Luna",
                  "picture": "img.jpg",
                  "breed": "Labrador",
                  "ageGroup": "ADULT",
                  "size": "MEDIUM",
                  "gender": "FEMALE",
                  "energyLevel": "HIGH",
                  "refugeId": "%s"
                }
                """.formatted(refugeId);

        PetRequest result = converter.convert(json);

        assertNotNull(result);
        assertEquals("Luna", result.getName());
        assertEquals("Labrador", result.getBreed());
        assertEquals(refugeId, result.getRefugeId());
    }

    @Test
    @DisplayName("convert() debe lanzar IllegalArgumentException con JSON inválido")
    void convert_jsonInvalido_lanzaIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class,
                () -> converter.convert("{json-malformado}"));
    }

    @Test
    @DisplayName("convert() debe lanzar IllegalArgumentException con cadena vacía")
    void convert_cadenaVacia_lanzaIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class,
                () -> converter.convert(""));
    }
}
