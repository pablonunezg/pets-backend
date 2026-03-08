package com.pumapunku.pet.domain.repository;

import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.domain.filters.Filter;

import java.util.List;
import java.util.Optional;

/**
 * Output port for user management in the domain layer.
 *
 * <p>Defines the data access contract for users that the infrastructure layer must implement.
 * As a domain interface, it has no dependencies on JPA or any concrete persistence technology.</p>
 */
public interface UserDomainRepository
{
    /**
     * Finds a user by their username.
     *
     * @param username username to search for.
     * @return an {@link Optional} containing the {@link UserDomain} if found.
     */
    Optional<UserDomain> findByUserName(String username);

    /**
     * Finds users that satisfy the criteria defined in the filter.
     *
     * @param filter domain filter with the search conditions; must not be {@code null}.
     * @return list of users that meet the criteria; may be empty.
     */
    List<UserDomain> findByFilter(Filter filter);
}
