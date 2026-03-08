package com.pumapunku.pet.application;

import com.pumapunku.pet.application.port.UploadFile;
import com.pumapunku.pet.domain.Pet;

import java.util.List;

/**
 * Use case: register a new pet in the system.
 *
 * <p>Defines the contract that implementations must fulfill in order to
 * persist a new pet and upload its images to storage.</p>
 *
 * <p>Acts as an input port in the hexagonal architecture,
 * decoupling the presentation layer from persistence details.</p>
 */
public interface CreatePetInteractor
{
    /**
     * Persists the pet, uploads its images and returns the complete entity.
     *
     * @param pet   domain entity with the pet data to create; must not be {@code null}.
     * @param files list of image files to upload; must not be {@code null}.
     * @return the persisted pet with its generated {@code id} and populated {@code picture} field.
     */
    Pet execute(Pet pet, List<UploadFile> files);
}
