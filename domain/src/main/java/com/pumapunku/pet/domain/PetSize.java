package com.pumapunku.pet.domain;

/**
 * Body size of a pet.
 *
 * <p>Values must match those stored in the {@code size} column
 * of the {@code pet} table: {@code SMALL}, {@code MEDIUM}, {@code LARGE}, {@code EXTRA_LARGE}.</p>
 */
public enum PetSize
{
    /**
     * Small (up to ~10 kg).
     */
    SMALL,

    /**
     * Medium (between 10 and 25 kg).
     */
    MEDIUM,

    /**
     * Large (between 25 and 45 kg).
     */
    LARGE,

    /**
     * Extra large (over 45 kg).
     */
    EXTRA_LARGE
}
