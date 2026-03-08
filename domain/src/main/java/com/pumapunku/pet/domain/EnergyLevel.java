package com.pumapunku.pet.domain;

/**
 * Energy level of a pet.
 *
 * <p>Values must match those stored in the {@code energy_level} column
 * of the {@code pet} table: {@code LOW}, {@code MODERATE}, {@code HIGH}.</p>
 */
public enum EnergyLevel
{
    /**
     * Low: calm animal that requires little physical activity.
     */
    LOW,

    /**
     * Moderate: active animal that requires regular exercise.
     */
    MODERATE,

    /**
     * High: very active animal that requires intense and frequent exercise.
     */
    HIGH
}
