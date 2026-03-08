package com.pumapunku.pet.domain;

/**
 * Current status of a pet in the system.
 *
 * <p>Values must match those stored in the {@code status} column
 * of the {@code pet} table: {@code FOR_ADOPTION}, {@code ADOPTED}, {@code MISSING}, {@code FOUND}.</p>
 */
public enum Status
{
    /**
     * The pet is available for adoption.
     */
    FOR_ADOPTION,

    /**
     * The pet has been adopted.
     */
    ADOPTED,

    /**
     * The pet has been reported as missing.
     */
    MISSING,

    /**
     * The missing pet has been found.
     */
    FOUND
}
