package com.pumapunku.pet.domain.filters;

/**
 * Filtro de condición simple que compara un campo con uno o dos valores.
 *
 * <p>Implementa el tipo hoja del patrón Composite para filtros.
 * Cuando el operador es {@link RelationalOperator#BETWEEN}, se usa
 * {@code value} como límite inferior y {@code secondValue} como límite superior.</p>
 *
 * @param <T>          tipo del valor a comparar.
 * @param field        nombre del campo (soporta notación punto para campos anidados).
 * @param operator     operador relacional a aplicar.
 * @param value        valor principal de la comparación.
 * @param secondValue  segundo valor para operadores como {@code BETWEEN}; puede ser {@code null}.
 */
public record ConditionFilter<T>(
        String field,
        RelationalOperator operator,
        T value,
        T secondValue
) implements Filter
{
    /**
     * Constructor de conveniencia para filtros de un solo valor.
     *
     * @param field    nombre del campo.
     * @param operator operador relacional.
     * @param value    valor de la comparación.
     */
    public ConditionFilter(String field, RelationalOperator operator, T value)
    {
        this(field, operator, value, null);
    }
}
