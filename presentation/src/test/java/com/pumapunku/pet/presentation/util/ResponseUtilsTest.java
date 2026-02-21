package com.pumapunku.pet.presentation.util;

import com.pumapunku.pet.domain.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link ResponseUtils}.
 */
@DisplayName("ResponseUtils")
class ResponseUtilsTest
{
    @Test
    @DisplayName("paginatedOk() debe retornar 200 con el body y X-Total-Count correcto")
    void paginatedOk_retornaStatusYHeader()
    {
        List<String> content = List.of("a", "b");
        Page<String> page = new Page<>(content, 1, 10, 42L);

        ResponseEntity<List<String>> response = ResponseUtils.paginatedOk(content, page);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(content, response.getBody());
        assertEquals("42", response.getHeaders().getFirst(ResponseUtils.HEADER_TOTAL_COUNT));
    }

    @Test
    @DisplayName("paginatedOk() con lista vacía debe tener X-Total-Count 0")
    void paginatedOk_listaVacia_headerCero()
    {
        Page<String> page = new Page<>(List.of(), 1, 10, 0L);

        ResponseEntity<List<String>> response = ResponseUtils.paginatedOk(List.of(), page);

        assertEquals("0", response.getHeaders().getFirst(ResponseUtils.HEADER_TOTAL_COUNT));
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("paginatedOk() debe funcionar con cualquier tipo genérico")
    void paginatedOk_tipoGenerico()
    {
        record Dto(int id) {}
        List<Dto> content = List.of(new Dto(1), new Dto(2), new Dto(3));
        Page<Dto> page = new Page<>(content, 1, 100, 3L);

        ResponseEntity<List<Dto>> response = ResponseUtils.paginatedOk(content, page);

        assertEquals(3, response.getBody().size());
        assertEquals("3", response.getHeaders().getFirst(ResponseUtils.HEADER_TOTAL_COUNT));
    }
}
