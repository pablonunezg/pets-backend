package com.pumapunku.pet.domain.filters;

/**
 * Relational operators available for condition filters.
 *
 * <p>Defines all comparison types that a {@link ConditionFilter} can express,
 * covering equality, numeric comparisons, ranges, collection membership,
 * and string comparisons.</p>
 */
public enum RelationalOperator
{
    /**
     * Equal to ({@code =}).
     */
    EQ,
    /**
     * Not equal to ({@code !=}).
     */
    NEQ,
    /**
     * Greater than ({@code >}).
     */
    GT,
    /**
     * Greater than or equal to ({@code >=}).
     */
    GTE,
    /**
     * Less than ({@code <}).
     */
    LT,
    /**
     * Less than or equal to ({@code <=}).
     */
    LTE,
    /**
     * Within a closed range {@code [value1, value2]}.
     */
    BETWEEN,
    /**
     * Value is contained in a collection ({@code IN}).
     */
    IN,
    /**
     * Value is NOT contained in a collection ({@code NOT IN}).
     */
    NOT_IN,
    /**
     * Field contains the given string ({@code LIKE %value%}).
     */
    LIKE,
    /**
     * Field starts with the given string ({@code LIKE value%}).
     */
    STARTS_WITH,
    /**
     * Field ends with the given string ({@code LIKE %value}).
     */
    ENDS_WITH,
    /**
     * Field is null ({@code IS NULL}).
     */
    IS_NULL,
    /**
     * Field is not null ({@code IS NOT NULL}).
     */
    IS_NOT_NULL
}
