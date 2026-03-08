package com.pumapunku.pet.application;

import com.pumapunku.pet.application.port.UploadFile;
import com.pumapunku.pet.domain.Pet;

import java.util.List;

/**
 * Use case: update the data of an existing pet.
 *
 * <p>Defines the contract for implementations that modify the attributes
 * of a pet already registered in the system.</p>
 *
 * <p>Acts as an input port in the hexagonal architecture.</p>
 */
public interface UpdatePetInteractor
{
    /**
     * Applies the changes contained in {@code pet} to the corresponding pet
     * and manages bucket files according to the received files.
     *
     * <p>Scenarios:</p>
     * <ul>
     *   <li><strong>With files</strong> ({@code files} not empty): deletes the current files
     *       from the bucket, uploads the new ones, and updates the {@code picture} column.</li>
     *   <li><strong>Without files</strong> ({@code files} empty or {@code null}): keeps
     *       the existing {@code picture}; only updates the database.</li>
     * </ul>
     *
     * @param pet   domain entity with the identifier and new data; must not be {@code null}.
     * @param files files to upload; may be {@code null} or empty to keep the current ones.
     * @throws com.pumapunku.pet.domain.exception.NotFoundException if no pet exists
     *                                                              with the given identifier.
     */
    void execute(Pet pet, List<UploadFile> files);
}
