package com.pumapunku.pet.domain.filters;

/**
 * Operadores relacionales disponibles para los filtros de condición.
 *
 * <p>Define todos los tipos de comparación que puede expresar un {@link ConditionFilter},
 * abarcando igualdad, comparaciones numéricas, rangos, pertenencia a colección
 * y comparaciones de cadenas.</p>
 */
public enum RelationalOperator
{
    /** Igual a ({@code =}). */
    EQ,
    /** Distinto de ({@code !=}). */
    NEQ,
    /** Mayor que ({@code >}). */
    GT,
    /** Mayor o igual que ({@code >=}). */
    GTE,
    /** Menor que ({@code <}). */
    LT,
    /** Menor o igual que ({@code <=}). */
    LTE,
    /** Dentro de un rango cerrado {@code [valor1, valor2]}. */
    BETWEEN,
    /** El valor está contenido en una colección ({@code IN}). */
    IN,
    /** El valor NO está contenido en una colección ({@code NOT IN}). */
    NOT_IN,
    /** El campo contiene la cadena indicada ({@code LIKE %valor%}). */
    LIKE,
    /** El campo comienza con la cadena indicada ({@code LIKE valor%}). */
    STARTS_WITH,
    /** El campo termina con la cadena indicada ({@code LIKE %valor}). */
    ENDS_WITH,
    /** El campo es nulo ({@code IS NULL}). */
    IS_NULL,
    /** El campo no es nulo ({@code IS NOT NULL}). */
    IS_NOT_NULL
}
