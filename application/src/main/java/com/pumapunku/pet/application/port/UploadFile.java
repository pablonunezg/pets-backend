package com.pumapunku.pet.application.port;

import java.io.InputStream;

/**
 * Representación agnóstica del framework de un archivo a subir.
 *
 * <p>Permite que la capa de aplicación reciba archivos sin depender de
 * {@code MultipartFile} de Spring Web ni de ningún otro framework HTTP.</p>
 *
 * <p>El contenido se expone como {@link InputStream} para permitir que la
 * capa de infraestructura lo transfiera de forma <em>streaming</em> al
 * proveedor de almacenamiento, evitando cargar el archivo completo en memoria.</p>
 *
 * @param content          stream de lectura del archivo; se consume una sola vez.
 * @param size             tamaño en bytes del archivo (necesario para el header {@code Content-Length}).
 * @param originalFilename nombre original del archivo.
 * @param contentType      tipo MIME del archivo (ej. {@code "image/jpeg"}).
 */
public record UploadFile(InputStream content, long size, String originalFilename, String contentType) {}
