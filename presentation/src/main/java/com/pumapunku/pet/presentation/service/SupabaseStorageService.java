package com.pumapunku.pet.presentation.service;

import com.pumapunku.pet.application.port.FileStoragePort;
import com.pumapunku.pet.application.port.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * Implementation of {@link FileStoragePort} that manages uploading and deleting
 * files in Supabase Storage.
 *
 * <p>Encapsulates communication with the Supabase REST API. Uses virtual threads
 * (Project Loom) to upload multiple files concurrently.</p>
 *
 * <p>The properties {@code supabase.url}, {@code supabase.apikey}, and
 * {@code supabase.bucket} must be defined in {@code application.properties}.</p>
 */
@Service
@RequiredArgsConstructor
public class SupabaseStorageService implements FileStoragePort
{
    /**
     * Supabase bucket base URL (must end with {@code /}).
     */
    @Value("${supabase.url}")
    private String url;

    /**
     * API key for authenticating with Supabase Storage.
     */
    @Value("${supabase.apikey}")
    private String apikey;

    /**
     * Name of the bucket where files will be stored.
     */
    @Value("${supabase.bucket}")
    private String bucket;

    /**
     * Stateless HTTP client for REST calls to Supabase.
     */
    private final RestClient restClient = RestClient.create();

    /**
     * {@inheritDoc}
     *
     * <p>Each file is uploaded in an independent virtual thread via
     * {@link CompletableFuture}. The method blocks until all futures have
     * completed and returns the public URLs of the uploaded files.</p>
     */
    @Override
    public List<String> uploadFiles(List<UploadFile> files, UUID uuid)
    {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor())
        {
            List<CompletableFuture<String>> futures = files.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> uploadFile(file, uuid), executor))
                    .toList();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Parses the comma-separated URL string and removes each file from the
     * Supabase bucket by sending a {@code DELETE} request per URL.</p>
     *
     * <p>The filename is extracted from the public URL by stripping the prefix
     * {@code <url>public/<bucket>/}. If the string is null or blank no operation
     * is performed.</p>
     */
    @Override
    public void deleteFiles(String pictureUrls)
    {
        if (pictureUrls == null || pictureUrls.isBlank())
        {
            return;
        }

        String publicPrefix = url + "public/" + bucket + "/";

        Arrays.stream(pictureUrls.split(","))
                .map(String::trim)
                .filter(u -> !u.isEmpty())
                .forEach(fileUrl -> deleteFile(fileUrl, publicPrefix));
    }

    @Override
    public String getBaseUrl()
    {
        return url + "public/" + bucket + "/";
    }

    /**
     * Converts a list of Spring {@link MultipartFile} objects to the port type
     * {@link UploadFile} so they can be processed by the application layer.
     *
     * @param multipartFiles files received in the HTTP request.
     * @return equivalent list of {@link UploadFile}.
     */
    public static List<UploadFile> toUploadFiles(List<MultipartFile> multipartFiles)
    {
        return multipartFiles.stream()
                .map(f ->
                {
                    try
                    {
                        return new UploadFile(f.getInputStream(), f.getSize(),
                                f.getOriginalFilename(), f.getContentType());
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("Error opening file stream: " + e.getMessage(), e);
                    }
                })
                .toList();
    }

    /**
     * Uploads a single {@link UploadFile} to Supabase Storage and returns its public URL.
     *
     * @param file file to upload.
     * @return public URL of the newly uploaded file.
     */
    private String uploadFile(UploadFile file, UUID uuid)
    {
        String fileName = file.originalFilename();
        String uploadUrl = url + bucket + "/" + uuid.toString() + "_" + fileName;

        InputStreamResource resource = new InputStreamResource(file.content())
        {
            @Override
            public long contentLength()
            {
                return file.size();
            }
        };

        restClient.post()
                .uri(uploadUrl)
                .header("authorization", "bearer " + apikey)
                .header("apikey", apikey)
                .header("x-upsert", "true")
                .contentType(MediaType.valueOf(file.contentType()))
                .body(resource)
                .retrieve()
                .toBodilessEntity();

        return url + "public/" + bucket + "/" + fileName;
    }

    /**
     * Deletes a single file from Supabase Storage given its public URL.
     *
     * <p>Constructs the deletion URL by extracting the filename from the public URL.</p>
     *
     * @param fileUrl      public URL of the file to delete.
     * @param publicPrefix public URL prefix ({@code <url>public/<bucket>/}).
     */
    private void deleteFile(String fileUrl, String publicPrefix)
    {
        String fileName = fileUrl.startsWith(publicPrefix)
                          ? fileUrl.substring(publicPrefix.length())
                          : fileUrl;

        String deleteUrl = url + bucket + "/" + fileName;

        restClient.delete()
                .uri(deleteUrl)
                .header("authorization", "bearer " + apikey)
                .header("apikey", apikey)
                .retrieve()
                .toBodilessEntity();
    }
}
