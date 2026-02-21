package com.pumapunku.pet.presentation.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link PaginatedResponse}.
 */
@DisplayName("PaginatedResponse")
class PaginatedResponseTest
{
    @Test
    @DisplayName("from() debe construir la respuesta con todos los campos incluyendo totalElements")
    void from_construyeCorrectamente()
    {
        List<String> content = List.of("a", "b", "c");

        PaginatedResponse<String> resp = PaginatedResponse.from(content, 1, 10, false, 5, 42L);

        assertSame(content, resp.getContent());
        assertEquals(1, resp.getPage());
        assertEquals(10, resp.getSize());
        assertFalse(resp.getIsLast());
        assertEquals(5, resp.getTotalPages());
        assertEquals(42L, resp.getTotalElements());
    }

    @Test
    @DisplayName("from() con isLast=true debe indicar última página")
    void from_isLastTrue()
    {
        PaginatedResponse<String> resp = PaginatedResponse.from(List.of(), 5, 10, true, 5, 50L);

        assertTrue(resp.getIsLast());
        assertEquals(5, resp.getPage());
        assertEquals(50L, resp.getTotalElements());
    }

    @Test
    @DisplayName("setters deben actualizar los campos")
    void setters_actualizanCampos()
    {
        PaginatedResponse<String> resp = new PaginatedResponse<>(List.of(), 1, 10, false, 1, 0L);
        resp.setPage(2);
        resp.setIsLast(true);
        resp.setTotalElements(100L);

        assertEquals(2, resp.getPage());
        assertTrue(resp.getIsLast());
        assertEquals(100L, resp.getTotalElements());
    }

    @Test
    @DisplayName("constructor completo debe asignar todos los campos")
    void constructor_asignaTodosLosCampos()
    {
        List<Integer> content = List.of(1, 2);
        PaginatedResponse<Integer> resp = new PaginatedResponse<>(content, 3, 20, false, 10, 200L);

        assertSame(content, resp.getContent());
        assertEquals(3, resp.getPage());
        assertEquals(20, resp.getSize());
        assertFalse(resp.getIsLast());
        assertEquals(10, resp.getTotalPages());
        assertEquals(200L, resp.getTotalElements());
    }
}
