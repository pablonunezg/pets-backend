package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.domain.filters.Filter;

import java.util.List;

/**
 * Custom repository fragment for dynamic user searches.
 *
 * <p>Defines the filter-based search contract that extends the standard
 * Spring Data JPA capabilities. The concrete implementation is in
 * {@link UserRepositoryImpl}, which Spring Data JPA auto-detects
 * thanks to the {@code Impl} suffix.</p>
 *
 * <p>This fragment is composed in {@link UserRepository} together with
 * {@link org.springframework.data.jpa.repository.JpaRepository} and
 * {@link com.pumapunku.pet.domain.repository.UserDomainRepository}.</p>
 */
public interface UserCustomRepository
{
    /**
     * Finds users that satisfy the criteria defined in the filter.
     *
     * @param filter domain filter with the search conditions; must not be {@code null}.
     * @return list of {@link UserDomain} that meet the criteria; may be empty.
     */
    List<UserDomain> findByFilter(Filter filter);
}
