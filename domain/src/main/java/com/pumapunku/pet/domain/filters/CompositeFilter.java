package com.pumapunku.pet.domain.filters;

import java.util.List;

/**
 * Composite filter that combines a list of sub-filters with a logical operator.
 *
 * <p>Implements the inner node of the Composite pattern for filters,
 * allowing arbitrarily deep trees of conditions to be built
 * connected by {@link LogicalOperator#AND} or {@link LogicalOperator#OR}.</p>
 *
 * @param operator logical operator that combines the sub-filters.
 * @param filters  list of sub-filters; must not be {@code null} or empty.
 */
public record CompositeFilter(
        LogicalOperator operator,
        List<Filter> filters
) implements Filter
{
}
