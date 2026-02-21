package com.pumapunku.pet.domain.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder fluido para construir filtros de consulta del dominio.
 *
 * <p>Simplifica la construcción de {@link CompositeFilter} encadenando
 * condiciones con un operador lógico común. Uso típico:</p>
 *
 * <pre>
 *   Filter filter = FilterBuilder.AND()
 *       .eq("username", "alice")
 *       .eq("role", "ADMIN")
 *       .build();
 * </pre>
 *
 * <p>Si solo se agrega una condición, el builder la retorna directamente
 * sin envolverla en un {@link CompositeFilter}.</p>
 */
public class FilterBuilder
{
    /** Operador lógico que conecta las condiciones acumuladas. */
    private final LogicalOperator operator;

    /** Lista de condiciones acumuladas durante la construcción. */
    private final List<Filter> filters = new ArrayList<>();

    /**
     * Constructor privado; usar los métodos de fábrica {@link #AND()} y {@link #OR()}.
     *
     * @param operator operador lógico a usar.
     */
    private FilterBuilder(LogicalOperator operator)
    {
        this.operator = operator;
    }

    /**
     * Crea un builder con operador lógico AND.
     *
     * @return nueva instancia de {@link FilterBuilder} configurada con AND.
     */
    public static FilterBuilder AND()
    {
        return new FilterBuilder(LogicalOperator.AND);
    }

    /**
     * Crea un builder con operador lógico OR.
     *
     * @return nueva instancia de {@link FilterBuilder} configurada con OR.
     */
    public static FilterBuilder OR()
    {
        return new FilterBuilder(LogicalOperator.OR);
    }

    /**
     * Agrega una condición de igualdad ({@code field = value}).
     *
     * @param field nombre del campo; soporta notación punto.
     * @param value valor esperado.
     * @return el mismo builder para encadenamiento fluido.
     */
    public FilterBuilder eq(String field, Object value)
    {
        filters.add(new ConditionFilter<>(field, RelationalOperator.EQ, value));
        return this;
    }

    /**
     * Agrega una condición de desigualdad ({@code field != value}).
     *
     * @param field nombre del campo.
     * @param value valor con el que no debe ser igual.
     * @return el mismo builder para encadenamiento fluido.
     */
    public FilterBuilder neq(String field, Object value)
    {
        filters.add(new ConditionFilter<>(field, RelationalOperator.NEQ, value));
        return this;
    }

    /**
     * Agrega una condición LIKE ({@code field LIKE %value%}).
     *
     * @param field nombre del campo.
     * @param value subcadena a buscar.
     * @return el mismo builder para encadenamiento fluido.
     */
    public FilterBuilder like(String field, String value)
    {
        filters.add(new ConditionFilter<>(field, RelationalOperator.LIKE, value));
        return this;
    }

    /**
     * Agrega un sub-filtro ya construido.
     *
     * @param filter filtro a añadir; no debe ser {@code null}.
     * @return el mismo builder para encadenamiento fluido.
     */
    public FilterBuilder add(Filter filter)
    {
        filters.add(filter);
        return this;
    }

    /**
     * Construye el filtro final.
     *
     * <p>Si solo hay una condición, la retorna directamente.
     * Si hay varias, las envuelve en un {@link CompositeFilter}.</p>
     *
     * @return filtro construido; nunca {@code null}.
     * @throws IllegalStateException si no se ha agregado ninguna condición.
     */
    public Filter build()
    {
        if (filters.isEmpty())
        {
            throw new IllegalStateException("El FilterBuilder no tiene condiciones.");
        }
        if (filters.size() == 1)
        {
            return filters.get(0);
        }
        return new CompositeFilter(operator, List.copyOf(filters));
    }
}
