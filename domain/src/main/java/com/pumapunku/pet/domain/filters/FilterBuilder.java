package com.pumapunku.pet.domain.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for constructing domain query filters.
 *
 * <p>Simplifies the construction of {@link CompositeFilter} by chaining
 * conditions with a common logical operator. Typical usage:</p>
 *
 * <pre>
 *   Filter filter = FilterBuilder.AND()
 *       .eq("username", "alice")
 *       .eq("role", "ADMIN")
 *       .build();
 * </pre>
 *
 * <p>If only one condition is added, the builder returns it directly
 * without wrapping it in a {@link CompositeFilter}.</p>
 */
public class FilterBuilder
{
    /**
     * Logical operator that connects the accumulated conditions.
     */
    private final LogicalOperator operator;

    /**
     * List of conditions accumulated during building.
     */
    private final List<Filter> filters = new ArrayList<>();

    /**
     * Private constructor; use the factory methods {@link #AND()} and {@link #OR()}.
     *
     * @param operator logical operator to use.
     */
    private FilterBuilder(LogicalOperator operator)
    {
        this.operator = operator;
    }

    /**
     * Creates a builder with the AND logical operator.
     *
     * @return new {@link FilterBuilder} instance configured with AND.
     */
    public static FilterBuilder AND()
    {
        return new FilterBuilder(LogicalOperator.AND);
    }

    /**
     * Creates a builder with the OR logical operator.
     *
     * @return new {@link FilterBuilder} instance configured with OR.
     */
    public static FilterBuilder OR()
    {
        return new FilterBuilder(LogicalOperator.OR);
    }

    /**
     * Adds an equality condition ({@code field = value}).
     *
     * @param field field name; supports dot notation.
     * @param value expected value.
     * @return this builder for method chaining.
     */
    public FilterBuilder eq(String field, Object value)
    {
        filters.add(new ConditionFilter<>(field, RelationalOperator.EQ, value));
        return this;
    }

    /**
     * Adds an inequality condition ({@code field != value}).
     *
     * @param field field name.
     * @param value value it must not be equal to.
     * @return this builder for method chaining.
     */
    public FilterBuilder neq(String field, Object value)
    {
        filters.add(new ConditionFilter<>(field, RelationalOperator.NEQ, value));
        return this;
    }

    /**
     * Adds a LIKE condition ({@code field LIKE %value%}).
     *
     * @param field field name.
     * @param value substring to search for.
     * @return this builder for method chaining.
     */
    public FilterBuilder like(String field, String value)
    {
        filters.add(new ConditionFilter<>(field, RelationalOperator.LIKE, value));
        return this;
    }

    /**
     * Adds an already-built sub-filter.
     *
     * @param filter filter to add; must not be {@code null}.
     * @return this builder for method chaining.
     */
    public FilterBuilder add(Filter filter)
    {
        filters.add(filter);
        return this;
    }

    /**
     * Builds the final filter.
     *
     * <p>If there is only one condition, it is returned directly.
     * If there are multiple, they are wrapped in a {@link CompositeFilter}.</p>
     *
     * @return the built filter; never {@code null}.
     * @throws IllegalStateException if no conditions have been added.
     */
    public Filter build()
    {
        if (filters.isEmpty())
        {
            throw new IllegalStateException("FilterBuilder has no conditions.");
        }
        if (filters.size() == 1)
        {
            return filters.get(0);
        }
        return new CompositeFilter(operator, List.copyOf(filters));
    }
}
