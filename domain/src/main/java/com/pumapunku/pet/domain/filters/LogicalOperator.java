package com.pumapunku.pet.domain.filters;

/**
 * Logical operators available for composite filters.
 *
 * <p>Used by {@link CompositeFilter} to indicate whether the sub-filters
 * are combined with a conjunction ({@code AND}) or a disjunction ({@code OR}).</p>
 */
public enum LogicalOperator
{
    /**
     * All sub-filters must be satisfied (logical conjunction).
     */
    AND,

    /**
     * At least one sub-filter must be satisfied (logical disjunction).
     */
    OR
}
