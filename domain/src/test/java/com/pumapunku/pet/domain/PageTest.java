package com.pumapunku.pet.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Page} y {@link PageRequest}.
 *
 * <p>Ambas clases usan numeración de páginas <strong>basada en 1</strong>.</p>
 */
@DisplayName("Page y PageRequest")
class PageTest
{
    // ── PageRequest ──────────────────────────────────────────────────────

    @Test
    @DisplayName("PageRequest - offset() debe ser (page - 1) * size")
    void pageRequest_offset_esPaginaMinusUnoMultiplicadoPorSize()
    {
        PageRequest req = new PageRequest(3, 10);
        assertEquals(20, req.offset()); // (3-1)*10 = 20
    }

    @Test
    @DisplayName("PageRequest - primera página (1) tiene offset 0")
    void pageRequest_paginaUno_offsetCero()
    {
        assertEquals(0, new PageRequest(1, 20).offset());
    }

    @Test
    @DisplayName("PageRequest - zeroBasedPage() devuelve page - 1")
    void pageRequest_zeroBasedPage_esPaginaMinusUno()
    {
        assertEquals(2, new PageRequest(3, 10).zeroBasedPage());
        assertEquals(0, new PageRequest(1, 10).zeroBasedPage());
    }

    @Test
    @DisplayName("PageRequest - almacena page y size correctamente")
    void pageRequest_camposCorrectos()
    {
        PageRequest req = new PageRequest(2, 5);
        assertEquals(2, req.page());
        assertEquals(5, req.size());
    }

    @Test
    @DisplayName("PageRequest - ofDefaults() retorna página 1 y tamaño 200")
    void pageRequest_ofDefaults_retornaValoresPorDefecto()
    {
        PageRequest defaults = PageRequest.ofDefaults();
        assertEquals(PageRequest.DEFAULT_PAGE, defaults.page());
        assertEquals(PageRequest.DEFAULT_SIZE, defaults.size());
    }

    // ── Page ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Page - totalPages() calcula correctamente páginas exactas")
    void page_totalPages_divisionExacta()
    {
        Page<String> page = new Page<>(List.of(), 1, 10, 30L);
        assertEquals(3, page.totalPages());
    }

    @Test
    @DisplayName("Page - totalPages() redondea hacia arriba")
    void page_totalPages_redondeoHaciaArriba()
    {
        Page<String> page = new Page<>(List.of(), 1, 10, 25L);
        assertEquals(3, page.totalPages());
    }

    @Test
    @DisplayName("Page - totalPages() con size 0 retorna 1")
    void page_totalPages_sizeZeroRetornaUno()
    {
        Page<String> page = new Page<>(List.of(), 1, 0, 100L);
        assertEquals(1, page.totalPages());
    }

    @Test
    @DisplayName("Page - isLast() retorna true en la última página (1-based)")
    void page_isLast_enUltimaPagina()
    {
        // 30 elementos, 10 por página → 3 páginas (1, 2, 3)
        Page<String> page = new Page<>(List.of(), 3, 10, 30L);
        assertTrue(page.isLast());
    }

    @Test
    @DisplayName("Page - isLast() retorna false en páginas intermedias")
    void page_isLast_enPaginaIntermedia()
    {
        Page<String> page = new Page<>(List.of(), 2, 10, 30L);
        assertFalse(page.isLast());
    }

    @Test
    @DisplayName("Page - isLast() retorna true cuando pageNumber supera totalPages")
    void page_isLast_cuandoPageNumberSuperaTotalPages()
    {
        Page<String> page = new Page<>(List.of(), 5, 10, 30L); // totalPages=3
        assertTrue(page.isLast());
    }

    @Test
    @DisplayName("Page - almacena content y totalElements correctamente")
    void page_camposCorrectos()
    {
        List<String> content = List.of("a", "b");
        Page<String> page = new Page<>(content, 1, 2, 10L);
        assertSame(content, page.content());
        assertEquals(10L, page.totalElements());
    }
}
