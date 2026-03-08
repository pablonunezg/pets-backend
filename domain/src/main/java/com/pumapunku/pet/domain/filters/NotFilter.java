package com.pumapunku.pet.domain.filters;

/**
 * Negation filter that inverts the result of another filter.
 *
 * <p>Wraps any {@link Filter} implementation and produces
 * the opposite predicate, equivalent to the logical {@code NOT} operator.</p>
 *
 * @param filter filter whose result will be negated; must not be {@code null}.
 */
public record NotFilter(Filter filter) implements Filter
{
}
