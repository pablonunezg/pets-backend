package com.pumapunku.pet.domain.filters;

/**
 * Sealed interface representing a domain query filter.
 *
 * <p>Defines the base type of the Composite pattern for building trees of
 * search conditions with no dependencies on JPA or other technologies.
 * The permitted concrete types are:</p>
 * <ul>
 *   <li>{@link ConditionFilter} — simple condition (field, operator, value).</li>
 *   <li>{@link CompositeFilter} — logical AND/OR combination of multiple filters.</li>
 *   <li>{@link NotFilter}       — negation of a filter.</li>
 * </ul>
 */
public sealed interface Filter permits ConditionFilter, CompositeFilter, NotFilter
{
}
