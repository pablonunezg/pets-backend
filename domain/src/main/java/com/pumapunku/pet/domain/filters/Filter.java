package com.pumapunku.pet.domain.filters;

/**
 * Interfaz sellada (sealed) que representa un filtro de consulta del dominio.
 *
 * <p>Define el tipo base del patrón Composite para construir árboles de
 * condiciones de búsqueda sin dependencias de JPA u otras tecnologías.
 * Los tipos concretos permitidos son:</p>
 * <ul>
 *   <li>{@link ConditionFilter} — condición simple (campo, operador, valor).</li>
 *   <li>{@link CompositeFilter} — combinación lógica AND/OR de varios filtros.</li>
 *   <li>{@link NotFilter}       — negación de un filtro.</li>
 * </ul>
 */
public sealed interface Filter permits ConditionFilter, CompositeFilter, NotFilter
{
}
