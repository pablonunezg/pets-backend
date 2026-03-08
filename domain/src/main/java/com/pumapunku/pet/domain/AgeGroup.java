package com.pumapunku.pet.domain;

/**
 * Age group of a pet.
 *
 * <p>Values must match those stored in the {@code age_group} column
 * of the {@code pet} table: {@code PUPPY}, {@code YOUNG}, {@code ADULT}, {@code SENIOR}.</p>
 */
public enum AgeGroup
{
    /**
     * Puppy / kitten (approximately under 6 months).
     */
    PUPPY,

    /**
     * Young (approximately between 6 months and 2 years).
     */
    YOUNG,

    /**
     * Adult (approximately between 2 and 8 years).
     */
    ADULT,

    /**
     * Senior (approximately over 8 years).
     */
    SENIOR
}
