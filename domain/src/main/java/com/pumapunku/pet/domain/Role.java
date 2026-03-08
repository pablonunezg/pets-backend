package com.pumapunku.pet.domain;

/**
 * Enumeration of available roles for system users.
 *
 * <p>Defines the access profiles that control the operations
 * each user can perform in the application.</p>
 *
 * <p>Values must match those stored in the {@code role} column
 * of the {@code app_user} table: {@code SUPER_ADMIN}, {@code ADMIN}, {@code NORMAL_USER}.</p>
 */
public enum Role
{
    /**
     * Role with full system permissions, including user management and configuration.
     */
    SUPER_ADMIN,

    /**
     * Role with permissions to manage pets and shelters.
     */
    ADMIN,

    /**
     * Role with basic read and registration permissions.
     */
    NORMAL_USER
}
