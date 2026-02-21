package com.pumapunku.pet.application;

import com.pumapunku.pet.application.port.UploadFile;
import com.pumapunku.pet.domain.Pet;

import java.util.List;

/**
 * Caso de uso: registrar una nueva mascota en el sistema.
 *
 * <p>Define el contrato que deben cumplir las implementaciones encargadas
 * de persistir una nueva mascota y subir sus imágenes al almacenamiento.</p>
 *
 * <p>Actúa como puerto de entrada (input port) en la arquitectura hexagonal,
 * desacoplando la capa de presentación de los detalles de persistencia.</p>
 */
public interface CreatePetInteractor
{
    /**
     * Persiste la mascota, sube sus imágenes y devuelve la entidad completa.
     *
     * @param pet   entidad de dominio con los datos de la mascota a crear; no debe ser {@code null}.
     * @param files lista de archivos de imagen a subir; no debe ser {@code null}.
     * @return la mascota ya persistida con su {@code id} generado y el campo {@code picture} poblado.
     */
    Pet execute(Pet pet, List<UploadFile> files);
}
