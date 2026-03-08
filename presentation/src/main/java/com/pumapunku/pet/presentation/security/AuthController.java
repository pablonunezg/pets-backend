package com.pumapunku.pet.presentation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for user authentication.
 *
 * <p>Exposes the {@code POST /auth/login} endpoint that receives user credentials,
 * validates them against Spring Security's {@link AuthenticationManager},
 * and returns a signed JWT token if authentication succeeds.</p>
 *
 * <p>Belongs to the presentation layer, within the security sub-package.</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController
{
    /**
     * Spring Security authentication manager.
     */
    private final AuthenticationManager authManager;

    /**
     * Service for loading user details by username.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Utility for generating and validating JWT tokens.
     */
    private final JwtUtil jwtUtil;

    /**
     * Password encoder used to compare credentials.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates the user with the received credentials and returns a JWT token.
     *
     * @param request object containing the username and password.
     * @return JWT token as a string.
     */
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request)
    {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        return jwtUtil.generateToken(userDetails);
    }

    /**
     * Internal record for the authentication response.
     *
     * @param token JWT token generated.
     * @param role  role of the authenticated user.
     */
    public record AuthResponse(
            String token,
            String role
    )
    {
    }
}
