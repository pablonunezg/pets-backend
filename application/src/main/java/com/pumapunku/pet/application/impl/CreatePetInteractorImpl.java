package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.CreatePetInteractor;
import com.pumapunku.pet.application.port.FileStoragePort;
import com.pumapunku.pet.application.port.UploadFile;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Implementación del caso de uso {@link CreatePetInteractor}.
 *
 * <p>Orquesta la creación de una mascota en dos pasos:</p>
 * <ol>
 *   <li>Persiste la mascota en la base de datos a través de {@link PetRepository},
 *       obteniendo el {@code id} generado.</li>
 *   <li>Sube los archivos de imagen al proveedor de almacenamiento a través de
 *       {@link FileStoragePort}, usando el {@code id} como prefijo de ruta.</li>
 * </ol>
 * <p>Finalmente, asocia las URLs de las imágenes a la mascota y la retorna.</p>
 *
 * <p>El bean es registrado en el contenedor de Spring mediante {@code @Named}
 * y sus dependencias se inyectan por constructor ({@code @RequiredArgsConstructor}).</p>
 */
@Named
@RequiredArgsConstructor
public class CreatePetInteractorImpl implements CreatePetInteractor
{
    /** Repositorio de dominio utilizado para persistir la nueva mascota. */
    private final transient PetRepository petRepository;

    /** Puerto de salida para subir los archivos de imagen. */
    private final transient FileStoragePort fileStoragePort;

    /**
     * {@inheritDoc}
     *
     * <p>Primero persiste la mascota para obtener su {@code id}, luego sube
     * las imágenes usando ese {@code id} como prefijo y almacena las URLs
     * resultantes en el campo {@code picture} antes de retornar.</p>
     */
    @Override
    public Pet execute(Pet pet, List<UploadFile> files)
    {
        Pet saved = petRepository.create(pet);

        List<String> urls = fileStoragePort.uploadFiles(files, saved.getId());
        saved.setPicture(String.join(",", urls));

        return saved;
    }
}
