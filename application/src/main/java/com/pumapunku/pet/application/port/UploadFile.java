package com.pumapunku.pet.application.port;

import java.io.InputStream;

/**
 * Framework-agnostic representation of a file to be uploaded.
 *
 * <p>Allows the application layer to receive files without depending on
 * Spring Web's {@code MultipartFile} or any other HTTP framework.</p>
 *
 * <p>The content is exposed as an {@link InputStream} to allow the
 * infrastructure layer to stream it to the storage provider,
 * avoiding loading the entire file into memory.</p>
 *
 * @param content          read stream of the file; consumed only once.
 * @param size             file size in bytes (required for the {@code Content-Length} header).
 * @param originalFilename original file name.
 * @param contentType      MIME type of the file (e.g. {@code "image/jpeg"}).
 */
public record UploadFile(InputStream content, long size, String originalFilename, String contentType)
{
}
