package com.pumapunku.pet.domain.filters;

/**
 * Filtro de negación que invierte el resultado de otro filtro.
 *
 * <p>Envuelve cualquier implementación de {@link Filter} y produce
 * el predicado opuesto, equivalente al operador lógico {@code NOT}.</p>
 *
 * @param filter filtro cuyo resultado será negado; no debe ser {@code null}.
 */
public record NotFilter(Filter filter) implements Filter
{
}
