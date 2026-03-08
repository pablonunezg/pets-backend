package com.pumapunku.pet.domain.filters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for domain filter types:
 * {@link ConditionFilter}, {@link CompositeFilter} y {@link NotFilter}.
 */
@DisplayName("Tipos de filtro del dominio")
class FilterTypesTest
{
    // ── ConditionFilter ──────────────────────────────────────────────────

    @Test
    @DisplayName("ConditionFilter - constructor de un valor no asigna secondValue")
    void conditionFilter_constructorUnValor_secondValueEsNull()
    {
        ConditionFilter<String> cf = new ConditionFilter<>("name", RelationalOperator.EQ, "Max");
        assertEquals("name", cf.field());
        assertEquals(RelationalOperator.EQ, cf.operator());
        assertEquals("Max", cf.value());
        assertNull(cf.secondValue());
    }

    @Test
    @DisplayName("ConditionFilter - constructor de dos valores asigna secondValue")
    void conditionFilter_constructorDosValores_asignaSecondValue()
    {
        ConditionFilter<Integer> cf = new ConditionFilter<>("age", RelationalOperator.BETWEEN, 1, 5);
        assertEquals(1, cf.value());
        assertEquals(5, cf.secondValue());
    }

    @Test
    @DisplayName("ConditionFilter - implementa Filter")
    void conditionFilter_implementaFilter()
    {
        assertInstanceOf(Filter.class,
                new ConditionFilter<>("field", RelationalOperator.IS_NULL, null));
    }

    // ── CompositeFilter ──────────────────────────────────────────────────

    @Test
    @DisplayName("CompositeFilter - almacena operador y lista de filtros")
    void compositeFilter_almacenaOperadorYFiltros()
    {
        ConditionFilter<String> f1 = new ConditionFilter<>("a", RelationalOperator.EQ, "x");
        ConditionFilter<String> f2 = new ConditionFilter<>("b", RelationalOperator.NEQ, "y");

        CompositeFilter comp = new CompositeFilter(LogicalOperator.AND, List.of(f1, f2));

        assertEquals(LogicalOperator.AND, comp.operator());
        assertEquals(2, comp.filters().size());
        assertTrue(comp.filters().contains(f1));
        assertTrue(comp.filters().contains(f2));
    }

    @Test
    @DisplayName("CompositeFilter - implementa Filter")
    void compositeFilter_implementaFilter()
    {
        assertInstanceOf(Filter.class,
                new CompositeFilter(LogicalOperator.OR, List.of()));
    }

    // ── NotFilter ────────────────────────────────────────────────────────

    @Test
    @DisplayName("NotFilter - almacena el filtro interno")
    void notFilter_almacenaFiltroInterno()
    {
        ConditionFilter<String> inner = new ConditionFilter<>("active", RelationalOperator.EQ, "true");
        NotFilter not = new NotFilter(inner);
        assertSame(inner, not.filter());
    }

    @Test
    @DisplayName("NotFilter - implementa Filter")
    void notFilter_implementaFilter()
    {
        NotFilter not = new NotFilter(new ConditionFilter<>("x", RelationalOperator.IS_NULL, null));
        assertInstanceOf(Filter.class, not);
    }

    // ── Enums ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("LogicalOperator - debe contener AND y OR")
    void logicalOperator_valores()
    {
        assertArrayEquals(
                new LogicalOperator[]{LogicalOperator.AND, LogicalOperator.OR},
                LogicalOperator.values()
        );
    }

    @Test
    @DisplayName("RelationalOperator - debe contener todos los operadores esperados")
    void relationalOperator_valoresEsperados()
    {
        RelationalOperator[] ops = RelationalOperator.values();
        assertEquals(14, ops.length);
        assertNotNull(RelationalOperator.valueOf("BETWEEN"));
        assertNotNull(RelationalOperator.valueOf("IS_NOT_NULL"));
    }
}
