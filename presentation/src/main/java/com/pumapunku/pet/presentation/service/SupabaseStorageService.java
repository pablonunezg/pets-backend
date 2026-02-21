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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * Implementación de {@link FileStoragePort} que gestiona la subida de archivos
 * a Supabase Storage.
 *
 * <p>Encapsula la comunicación con la API REST de Supabase. Utiliza virtual threads
 * (Project Loom) para subir múltiples archivos de forma concurrente, minimizando
 * la latencia total cuando se procesan lotes de imágenes.</p>
 *
 * <p>Las propiedades {@code supabase.url}, {@code supabase.apikey} y
 * {@code supabase.bucket} deben estar definidas en {@code application.properties}.</p>
 */
@Service
@RequiredArgsConstructor
public class SupabaseStorageService implements FileStoragePort
{
    /** URL base del bucket de Supabase (debe terminar con {@code /}). */
    @Value("${supabase.url}")
    private String url;

    /** Clave de API para autenticarse con Supabase Storage. */
    @Value("${supabase.apikey}")
    private String apikey;

    /** Nombre del bucket donde se almacenarán los archivos. */
    @Value("${supabase.bucket}")
    private String bucket;

    /** Cliente HTTP sin estado para las llamadas REST a Supabase. */
    private final RestClient restClient = RestClient.create();

    /**
     * {@inheritDoc}
     *
     * <p>Cada archivo es subido en un virtual thread independiente mediante
     * {@link CompletableFuture}. El método bloquea hasta que todos los futuros
     * hayan completado y retorna las URLs públicas de los archivos subidos.</p>
     */
    @Override
    public List<String> uploadFiles(List<UploadFile> files, UUID petId)
    {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor())
        {
            List<CompletableFuture<String>> futures = files.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> uploadFile(file, petId), executor))
                    .toList();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
        }
    }

    /**
     * Convierte una lista de {@link MultipartFile} de Spring al tipo de puerto
     * {@link UploadFile} para que puedan ser procesados por la capa de aplicación.
     *
     * <p>Usa {@link MultipartFile#getInputStream()} en lugar de {@link MultipartFile#getBytes()}
     * para evitar cargar el contenido completo en memoria: el stream se leerá de forma
     * perezosa durante la transferencia a Supabase.</p>
     *
     * @param multipartFiles archivos recibidos en la petición HTTP.
     * @return lista de {@link UploadFile} equivalente.
     */
    public static List<UploadFile> toUploadFiles(List<MultipartFile> multipartFiles)
    {
        return multipartFiles.stream()
                .map(f -> {
                    try {
                        return new UploadFile(f.getInputStream(), f.getSize(),
                                f.getOriginalFilename(), f.getContentType());
                    } catch (IOException e) {
                        throw new RuntimeException("Error abriendo stream del archivo: " + e.getMessage(), e);
                    }
                })
                .toList();
    }

    /**
     * Sube un único {@link UploadFile} a Supabase Storage y retorna su URL pública.
     *
     * <p>El nombre del archivo resultante sigue el patrón {@code <uuid>_<nombreOriginal>}.
     * Si el archivo ya existe, se sobreescribe ({@code x-upsert: true}).</p>
     *
     * <p>El contenido se envía mediante {@link InputStreamResource} para que
     * {@code RestClient} transfiera los bytes directamente desde el stream,
     * sin cargarlos en memoria. El header {@code Content-Length} se establece
     * explícitamente para que el servidor pueda gestionar la recepción correctamente.</p>
     *
     * @param file  archivo a subir.
     * @param petId identificador de la mascota propietaria del archivo.
     * @return URL pública del archivo recién subido.
     */
    private String uploadFile(UploadFile file, UUID petId)
    {
        String fileName = petId + "_" + file.originalFilename();
        String uploadUrl = url + bucket + "/" + fileName;

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
}
