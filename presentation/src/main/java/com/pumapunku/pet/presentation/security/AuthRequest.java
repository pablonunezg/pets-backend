package com.pumapunku.pet.presentation.security;

/**
 * Data Transfer Object that encapsulates authentication credentials.
 *
 * <p>Received in the body of the {@code POST /auth/login} endpoint and
 * contains the username and plain-text password that
 * will be validated by Spring Security.</p>
 *
 * @param username username.
 * @param password plain-text password.
 */
public record AuthRequest(String username, String password)
{
}
