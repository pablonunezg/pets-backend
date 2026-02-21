package com.pumapunku.pet.domain.filters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link FilterBuilder}.
 */
@DisplayName("FilterBuilder")
class FilterBuilderTest
{
    // ── AND / OR factory ────────────────────────────────────────────────────

    @Test
    @DisplayName("AND() debe crear builder con operador AND")
    void and_creaBuilderConOperadorAnd()
    {
        FilterBuilder builder = FilterBuilder.AND();
        assertNotNull(builder);
    }

    @Test
    @DisplayName("OR() debe crear builder con operador OR")
    void or_creaBuilderConOperadorOr()
    {
        FilterBuilder builder = FilterBuilder.OR();
        assertNotNull(builder);
    }

    // ── Condición única → retorna ConditionFilter ────────────────────────

    @Test
    @DisplayName("build() con una condición retorna ConditionFilter directamente")
    void build_unaCondicion_retornaConditionFilter()
    {
        Filter result = FilterBuilder.AND().eq("name", "Luna").build();
        assertInstanceOf(ConditionFilter.class, result);
        ConditionFilter<?> cf = (ConditionFilter<?>) result;
        assertEquals("name", cf.field());
        assertEquals(RelationalOperator.EQ, cf.operator());
        assertEquals("Luna", cf.value());
    }

    // ── Múltiples condiciones → CompositeFilter ──────────────────────────

    @Test
    @DisplayName("build() con varias condiciones retorna CompositeFilter AND")
    void build_variasCondicionesAnd_retornaCompositeAnd()
    {
        Filter result = FilterBuilder.AND()
                .eq("name", "Luna")
                .eq("breed", "Labrador")
                .build();

        assertInstanceOf(CompositeFilter.class, result);
        CompositeFilter comp = (CompositeFilter) result;
        assertEquals(LogicalOperator.AND, comp.operator());
        assertEquals(2, comp.filters().size());
    }

    @Test
    @DisplayName("build() con varias condiciones retorna CompositeFilter OR")
    void build_variasCondicionesOr_retornaCompositeOr()
    {
        Filter result = FilterBuilder.OR()
                .eq("size", "SMALL")
                .eq("size", "MEDIUM")
                .build();

        assertInstanceOf(CompositeFilter.class, result);
        assertEquals(LogicalOperator.OR, ((CompositeFilter) result).operator());
    }

    // ── neq ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("neq() debe agregar condición NEQ")
    void neq_debeAgregarCondicionNEQ()
    {
        Filter result = FilterBuilder.AND().neq("status", "DELETED").build();
        ConditionFilter<?> cf = (ConditionFilter<?>) result;
        assertEquals(RelationalOperator.NEQ, cf.operator());
        assertEquals("DELETED", cf.value());
    }

    // ── like ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("like() debe agregar condición LIKE")
    void like_debeAgregarCondicionLIKE()
    {
        Filter result = FilterBuilder.AND().like("name", "Lu").build();
        ConditionFilter<?> cf = (ConditionFilter<?>) result;
        assertEquals(RelationalOperator.LIKE, cf.operator());
        assertEquals("Lu", cf.value());
    }

    // ── add ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("add() debe incorporar un filtro ya construido")
    void add_debeAgregarSubFiltro()
    {
        Filter sub = new ConditionFilter<>("age", RelationalOperator.GT, 2);
        Filter result = FilterBuilder.AND()
                .eq("name", "Max")
                .add(sub)
                .build();

        CompositeFilter comp = (CompositeFilter) result;
        assertEquals(2, comp.filters().size());
        assertTrue(comp.filters().contains(sub));
    }

    // ── sin condiciones → excepción ──────────────────────────────────────

    @Test
    @DisplayName("build() sin condiciones debe lanzar IllegalStateException")
    void build_sinCondiciones_lanzaExcepcion()
    {
        FilterBuilder builder = FilterBuilder.AND();
        assertThrows(IllegalStateException.class, builder::build);
    }

    // ── inmutabilidad de la lista del CompositeFilter ────────────────────

    @Test
    @DisplayName("el CompositeFilter construido debe tener lista inmutable")
    void build_compositeFilter_listaEsInmutable()
    {
        Filter result = FilterBuilder.AND()
                .eq("a", 1)
                .eq("b", 2)
                .build();

        CompositeFilter comp = (CompositeFilter) result;
        List<Filter> filters = comp.filters();
        assertThrows(UnsupportedOperationException.class, () -> filters.add(new NotFilter(filters.get(0))));
    }
}
