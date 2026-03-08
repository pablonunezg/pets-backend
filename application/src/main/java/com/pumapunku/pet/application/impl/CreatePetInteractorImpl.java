package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.CreatePetInteractor;
import com.pumapunku.pet.application.port.FileStoragePort;
import com.pumapunku.pet.application.port.UploadFile;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link CreatePetInteractor} use case.
 *
 * <p>Orchestrates pet creation in two steps:</p>
 * <ol>
 *   <li>Persists the pet in the database via {@link PetRepository},
 *       obtaining the generated {@code id}.</li>
 *   <li>Uploads the image files to the storage provider via
 *       {@link FileStoragePort}, using the {@code id} as the path prefix.</li>
 * </ol>
 * <p>Finally, associates the image URLs with the pet and returns it.</p>
 *
 * <p>The bean is registered in the Spring container via {@code @Named}
 * and its dependencies are injected by constructor ({@code @RequiredArgsConstructor}).</p>
 */
@Named
@RequiredArgsConstructor
public class CreatePetInteractorImpl implements CreatePetInteractor
{
    /**
     * Domain repository used to persist the new pet.
     */
    private final transient PetRepository petRepository;

    /**
     * Output port for uploading image files.
     */
    private final transient FileStoragePort fileStoragePort;

    /**
     * {@inheritDoc}
     *
     * <p>First persists the pet to obtain its {@code id}, then uploads
     * the images using that {@code id} as a prefix, and stores the resulting
     * URLs in the {@code picture} field before returning.</p>
     */
    @Override
    public Pet execute(Pet pet, List<UploadFile> files)
    {
        String baseUrl = fileStoragePort.getBaseUrl();

        String picture = files.stream().map(UploadFile::originalFilename).collect(Collectors.joining(","));
        pet.setPicture("baseUrl" + baseUrl + "," + picture);

        Pet petSaved = petRepository.create(pet);

        fileStoragePort.uploadFiles(files, petSaved.getId());

        return petSaved;
    }
}
