package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.UpdatePetInteractor;
import com.pumapunku.pet.application.port.FileStoragePort;
import com.pumapunku.pet.application.port.UploadFile;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.exception.NotFoundException;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Implementation of the {@link UpdatePetInteractor} use case.
 *
 * <p>Orchestrates pet update in two scenarios:</p>
 *
 * <ul>
 *   <li><strong>With files</strong>: fetches the existing pet to read its current
 *       {@code picture}, deletes those files from the bucket via
 *       {@link FileStoragePort}, uploads the new files and updates the database
 *       with the new {@code picture}.</li>
 *   <li><strong>Without files</strong>: fetches the existing pet, keeps its
 *       {@code picture} and updates only the database.</li>
 * </ul>
 *
 * <p>The bean is registered in Spring via {@code @Named}
 * and injected by constructor.</p>
 */
@Named
@RequiredArgsConstructor
public class UpdatePetInteractorImpl implements UpdatePetInteractor
{
    /**
     * Domain repository for reading and updating pet data.
     */
    private final transient PetRepository petRepository;

    /**
     * Output port for uploading and deleting files from the bucket.
     */
    private final transient FileStoragePort fileStoragePort;

    /**
     * {@inheritDoc}
     *
     * <p>Always fetches the existing pet to verify it exists
     * ({@link NotFoundException} if not) and to read its current {@code picture}.</p>
     */
    @Override
    public void execute(Pet pet, List<UploadFile> files)
    {
        Pet existing = petRepository.findById(pet.getId())
                .orElseThrow(() -> new NotFoundException("Pet", pet.getId().toString()));

        if (files != null && !files.isEmpty())
        {
            // Scenario 1: replace bucket files
            fileStoragePort.deleteFiles(existing.getPicture());

            List<String> urls = fileStoragePort.uploadFiles(files, pet.getId());
            pet.setPicture(String.join(",", urls));
        }
        else
        {
            // Scenario 2: keep existing files, do not touch the bucket
            pet.setPicture(existing.getPicture());
        }

        petRepository.update(pet);
    }
}
