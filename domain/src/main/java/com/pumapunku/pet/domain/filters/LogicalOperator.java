package com.pumapunku.pet.domain.filters;

/**
 * Operadores lógicos disponibles para los filtros compuestos.
 *
 * <p>Utilizado por {@link CompositeFilter} para indicar si los sub-filtros
 * se combinan con una conjunción ({@code AND}) o una disyunción ({@code OR}).</p>
 */
public enum LogicalOperator
{
    /** Todos los sub-filtros deben cumplirse (conjunción lógica). */
    AND,

    /** Al menos un sub-filtro debe cumplirse (disyunción lógica). */
    OR
}
