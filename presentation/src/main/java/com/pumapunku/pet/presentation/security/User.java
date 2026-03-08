package com.pumapunku.pet.presentation.security;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Security model that implements Spring Security's {@link UserDetails}.
 *
 * <p>Acts as an adapter between the user domain object and the
 * contract required by Spring Security to manage authentication
 * and authorization.</p>
 *
 * <p>The {@code isLocked} flag comes from the {@code is_locked} column of
 * the {@code app_user} table and controls the result of {@link #isAccountNonLocked()}.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails
{
    /**
     * Unique user identifier (UUID from the {@code app_user} table).
     */
    @NotEmpty
    @NotNull
    private UUID userId;

    /**
     * Unique username in the system.
     */
    private String username;

    /**
     * Password encoded with BCrypt.
     */
    private String password;

    /**
     * User role (e.g., {@code ADMIN} or {@code NORMAL_USER}).
     */
    private String role;

    /**
     * Indicates whether the user account is locked.
     *
     * <p>When {@code true}, Spring Security rejects the user's authentication
     * with a {@code LockedException}.</p>
     */
    private boolean locked;

    /**
     * Returns the list of authorities granted to the user.
     *
     * @return collection with a single authority of type {@code ROLE_<role>}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    /**
     * @return {@code true} — the account never expires in this implementation.
     */
    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    /**
     * Returns whether the account is NOT locked.
     *
     * @return {@code true} if {@code locked} is {@code false}; {@code false} otherwise.
     */
    @Override
    public boolean isAccountNonLocked()
    {
        return !locked;
    }

    /**
     * @return {@code true} — credentials never expire in this implementation.
     */
    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    /**
     * @return {@code true} — the user is always enabled in this implementation.
     */
    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
