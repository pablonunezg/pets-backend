package com.pumapunku.pet.application;

import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;

/**
 * Use case: retrieve the paginated list of pets.
 *
 * <p>Defines the contract that implementations must fulfill in order to
 * retrieve registered pets in a paginated manner.</p>
 *
 * <p>Belongs to the application layer following Clean Architecture principles,
 * acting as an input port.</p>
 */
public interface GetPetsInteractor
{
    /**
     * Executes the use case and returns a page of pets.
     *
     * <p>If no explicit pagination is required, use
     * {@link PageRequest#ofDefaults()} to get the default values
     * (page 1, size 200).</p>
     *
     * @param pageRequest pagination parameters (1-based page and size); must not be {@code null}.
     * @return {@link Page} with the pets for the requested page; never {@code null}.
     */
    Page<Pet> execute(PageRequest pageRequest);
}
