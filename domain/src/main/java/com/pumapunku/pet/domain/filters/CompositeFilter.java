package com.pumapunku.pet.domain.filters;

import java.util.List;

/**
 * Filtro compuesto que combina una lista de sub-filtros con un operador lógico.
 *
 * <p>Implementa el nodo interno del patrón Composite para filtros,
 * permitiendo construir árboles arbitrariamente profundos de condiciones
 * conectadas por {@link LogicalOperator#AND} u {@link LogicalOperator#OR}.</p>
 *
 * @param operator operador lógico que combina los sub-filtros.
 * @param filters  lista de sub-filtros; no debe ser {@code null} ni vacía.
 */
public record CompositeFilter(
        LogicalOperator operator,
        List<Filter> filters
) implements Filter
{
}
