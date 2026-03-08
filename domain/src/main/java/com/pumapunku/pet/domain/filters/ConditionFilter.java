package com.pumapunku.pet.domain.filters;

/**
 * Simple condition filter that compares a field against one or two values.
 *
 * <p>Implements the leaf node of the Composite pattern for filters.
 * When the operator is {@link RelationalOperator#BETWEEN}, {@code value}
 * is used as the lower bound and {@code secondValue} as the upper bound.</p>
 *
 * @param <T>         type of the value to compare.
 * @param field       field name (supports dot notation for nested fields).
 * @param operator    relational operator to apply.
 * @param value       primary comparison value.
 * @param secondValue second value for operators such as {@code BETWEEN}; may be {@code null}.
 */
public record ConditionFilter<T>(
        String field,
        RelationalOperator operator,
        T value,
        T secondValue
) implements Filter
{
    /**
     * Convenience constructor for single-value filters.
     *
     * @param field    field name.
     * @param operator relational operator.
     * @param value    comparison value.
     */
    public ConditionFilter(String field, RelationalOperator operator, T value)
    {
        this(field, operator, value, null);
    }
}
