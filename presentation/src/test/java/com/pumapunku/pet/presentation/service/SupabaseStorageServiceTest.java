package com.pumapunku.pet.presentation.service;

import com.pumapunku.pet.application.port.UploadFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SupabaseStorageService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SupabaseStorageService")
class SupabaseStorageServiceTest
{
    private static final String BASE_URL = "https://example.supabase.co/storage/v1/object/";
    private static final String BUCKET = "pets";

    @InjectMocks
    private SupabaseStorageService service;

    @BeforeEach
    void setUp()
    {
        ReflectionTestUtils.setField(service, "url", BASE_URL);
        ReflectionTestUtils.setField(service, "apikey", "test-api-key");
        ReflectionTestUtils.setField(service, "bucket", BUCKET);
    }

    @Test
    @DisplayName("toUploadFiles() debe lanzar RuntimeException cuando un MultipartFile no puede leerse")
    void toUploadFiles_errorDeIO_lanzaRuntimeException() throws IOException
    {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("disco lleno"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> SupabaseStorageService.toUploadFiles(List.of(file)));

        assertTrue(ex.getMessage().contains("Error abriendo stream del archivo"));
    }

    @Test
    @DisplayName("toUploadFiles() debe convertir correctamente un MultipartFile a UploadFile")
    void toUploadFiles_convierteCorrectamente() throws IOException
    {
        byte[] expectedBytes = {1, 2, 3};
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(expectedBytes));
        when(file.getSize()).thenReturn((long) expectedBytes.length);
        when(file.getOriginalFilename()).thenReturn("foto.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");

        List<UploadFile> result = SupabaseStorageService.toUploadFiles(List.of(file));

        assertEquals(1, result.size());
        UploadFile uploadFile = result.get(0);
        assertEquals("foto.jpg", uploadFile.originalFilename());
        assertEquals("image/jpeg", uploadFile.contentType());
        assertEquals(expectedBytes.length, uploadFile.size());
        assertArrayEquals(expectedBytes, uploadFile.content().readAllBytes());
    }

    @Test
    @DisplayName("uploadFiles() with empty list should return empty list")
    void uploadFiles_listaVacia_retornaListaVacia()
    {
        List<String> result = service.uploadFiles(List.of(), UUID.randomUUID());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deleteFiles() with null pictureUrls should not throw exception")
    void deleteFiles_pictureUrlsNulo_noHaceNada()
    {
        assertDoesNotThrow(() -> service.deleteFiles(null));
    }

    @Test
    @DisplayName("deleteFiles() with empty string should not throw exception")
    void deleteFiles_cadenaVacia_noHaceNada()
    {
        assertDoesNotThrow(() -> service.deleteFiles(""));
    }

    @Test
    @DisplayName("deleteFiles() with blank string should not throw exception")
    void deleteFiles_cadenaEnBlanco_noHaceNada()
    {
        assertDoesNotThrow(() -> service.deleteFiles("   "));
    }
}
