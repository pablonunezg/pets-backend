package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.domain.repository.UserDomainRepository;
import com.pumapunku.pet.infrastructure.mapper.UserMapper;
import com.pumapunku.pet.infrastructure.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/**
 * Primary user repository that combines multiple capabilities.
 *
 * <p>Extends:</p>
 * <ul>
 *   <li>{@link JpaRepository} — standard Spring Data JPA CRUD operations.</li>
 *   <li>{@link JpaSpecificationExecutor} — support for Specification-based queries.</li>
 *   <li>{@link UserCustomRepository} — dynamic filter-based domain searches.</li>
 *   <li>{@link UserDomainRepository} — output port required by the domain.</li>
 * </ul>
 *
 * <p>The {@link #findByUserName(String)} method adapts the JPA response to the
 * domain model via {@link UserMapper}, keeping the domain free of infrastructure
 * dependencies.</p>
 */
public interface UserRepository extends JpaRepository<User, UUID>,
        JpaSpecificationExecutor<User>,
        UserCustomRepository,
        UserDomainRepository
{
    /**
     * Finds a user by username at the JPA entity level.
     *
     * @param username username to search for.
     * @return an {@link Optional} containing the {@link User} entity if it exists.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by username and converts it to the domain model.
     *
     * <p>Implements the method required by {@link UserDomainRepository}.</p>
     *
     * @param username username to search for.
     * @return an {@link Optional} containing the corresponding {@link UserDomain}, if it exists.
     */
    default Optional<UserDomain> findByUserName(String username)
    {
        return findByUsername(username).map(UserMapper.INSTANCE::toUserDomain);
    }
}
