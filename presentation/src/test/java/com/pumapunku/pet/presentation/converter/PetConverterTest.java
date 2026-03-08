package com.pumapunku.pet.presentation.converter;

import com.pumapunku.pet.domain.Status;
import com.pumapunku.pet.presentation.request.PetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("convert() should deserialize valid JSON into PetRequest")
    void convert_jsonValido_retornaPetRequest()
    {
        String json = """
                {
                  "name": "Luna",
                  "description": "Perrita muy amigable",
                  "picture": "img.jpg",
                  "breed": "Labrador",
                  "ageGroup": "ADULT",
                  "petSize": "MEDIUM",
                  "gender": "FEMALE",
                  "energyLevel": "HIGH",
                  "status": "FOR_ADOPTION"
                }
                """;

        PetRequest result = converter.convert(json);

        assertNotNull(result);
        assertEquals("Luna", result.getName());
        assertEquals("Perrita muy amigable", result.getDescription());
        assertEquals("Labrador", result.getBreed());
        assertEquals(Status.FOR_ADOPTION, result.getStatus());
    }

    @Test
    @DisplayName("convert() should throw IllegalArgumentException for invalid JSON")
    void convert_jsonInvalido_lanzaIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class,
                () -> converter.convert("{json-malformado}"));
    }

    @Test
    @DisplayName("convert() should throw IllegalArgumentException for empty string")
    void convert_cadenaVacia_lanzaIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class,
                () -> converter.convert(""));
    }
}
