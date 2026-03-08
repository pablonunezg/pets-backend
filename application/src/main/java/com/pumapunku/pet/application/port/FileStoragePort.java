package com.pumapunku.pet.application.port;

import java.util.List;
import java.util.UUID;

/**
 * Output port for file storage.
 *
 * <p>Defines the contract that any storage provider
 * (Supabase, S3, GCS, etc.) must fulfill without the application layer
 * knowing the implementation details.</p>
 */
public interface FileStoragePort
{
    /**
     * Uploads a list of files associated with a pet and returns their public URLs.
     *
     * @param files list of files to upload; must not be {@code null}.
     * @return list of public URLs of the uploaded files, in the same order received.
     */
    List<String> uploadFiles(List<UploadFile> files, UUID uuid);

    /**
     * Deletes the bucket files associated with a pet.
     *
     * <p>The URLs must be the same public URLs returned by {@link #uploadFiles}
     * at upload time. If {@code pictureUrls} is null or empty, no operation is performed.</p>
     *
     * @param pictureUrls comma-separated string of file URLs to delete
     *                    (format stored in {@code pet.picture}).
     */
    void deleteFiles(String pictureUrls);

    String getBaseUrl();
}
