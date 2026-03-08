package com.pumapunku.pet.presentation.security;

import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.domain.filters.Filter;
import com.pumapunku.pet.domain.filters.FilterBuilder;
import com.pumapunku.pet.domain.repository.UserDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link UserDetailsService} that loads user details
 * from the domain repository.
 *
 * <p>Spring Security calls this service during the authentication process to
 * obtain a {@link UserDetails} object from the provided username.</p>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService
{
    /**
     * Domain repository for finding users by username.
     */
    private final UserDomainRepository userDomainRepository;

    /**
     * Loads the security details of the user identified by {@code username}.
     *
     * <p>The domain's {@code locked} field is propagated to the security model
     * so Spring Security can block authentication if applicable.</p>
     *
     * @param username username to search for.
     * @return {@link UserDetails} object ready for the authentication process.
     * @throws UsernameNotFoundException if no user with that username exists.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Filter filter = FilterBuilder.AND()
                .eq("username", username)
                .build();

        List<UserDomain> users = userDomainRepository.findByFilter(filter);

        Optional<User> ui = userDomainRepository
                .findByUserName(username)
                .map(u -> User.builder()
                        .userId(u.getId())
                        .username(u.getUsername())
                        .password(u.getPassword())
                        .role(u.getRole().toString())
                        .locked(u.isLocked())
                        .build());

        return ui.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
