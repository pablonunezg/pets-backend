package com.pumapunku.pet.application.port;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de salida (output port) para el almacenamiento de archivos.
 *
 * <p>Define el contrato que debe cumplir cualquier proveedor de almacenamiento
 * (Supabase, S3, GCS, etc.) sin que la capa de aplicación conozca los detalles
 * de implementación.</p>
 */
public interface FileStoragePort
{
    /**
     * Sube una lista de archivos asociados a una mascota y retorna sus URLs públicas.
     *
     * @param files lista de archivos a subir; no debe ser {@code null}.
     * @param petId identificador de la mascota propietaria de los archivos.
     * @return lista de URLs públicas de los archivos subidos, en el mismo orden recibido.
     */
    List<String> uploadFiles(List<UploadFile> files, UUID petId);
}
