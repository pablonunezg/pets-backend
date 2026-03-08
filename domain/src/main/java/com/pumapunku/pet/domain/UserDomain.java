package com.pumapunku.pet.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain entity representing a system user.
 *
 * <p>Contains the minimum data required for authentication and authorization.
 * As a domain class, it has no dependencies on JPA, Spring Security,
 * or any other infrastructure library.</p>
 *
 * <p>The infrastructure layer maps this class to/from the JPA entity
 * {@code User} using {@code UserMapper}.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDomain
{
    /**
     * Unique identifier of the user.
     */
    private UUID id;

    /**
     * Unique username used for authentication.
     */
    private String username;

    /**
     * Password encoded with BCrypt.
     */
    private String password;

    /**
     * Role that determines the user's permissions in the system.
     */
    private Role role;

    /**
     * Indicates whether the user account is locked.
     *
     * <p>When {@code true}, the user cannot authenticate in the system.
     * Persisted in the {@code is_locked} column of the {@code app_user} table.</p>
     */
    private boolean locked;
}
