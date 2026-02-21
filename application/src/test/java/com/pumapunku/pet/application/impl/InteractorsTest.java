package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.port.FileStoragePort;
import com.pumapunku.pet.application.port.UploadFile;
import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.repository.PetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para los cuatro interactores de la capa de aplicación.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Interactores de aplicación")
class InteractorsTest
{
    // ── CreatePetInteractorImpl ──────────────────────────────────────────

    @Nested
    @DisplayName("CreatePetInteractorImpl")
    class CreatePetInteractorImplTest
    {
        @Mock
        private PetRepository petRepository;

        @Mock
        private FileStoragePort fileStoragePort;

        @InjectMocks
        private CreatePetInteractorImpl interactor;

        @Test
        @DisplayName("execute() debe persistir, subir archivos y retornar el pet con picture")
        void execute_persisteSubeArchivosYRetornaPet()
        {
            Pet input = new Pet();
            input.setName("Luna");

            Pet saved = new Pet();
            saved.setId(UUID.randomUUID());
            saved.setName("Luna");

            byte[] content = {1};
            InputStream stream = new ByteArrayInputStream(content);
            UploadFile file = new UploadFile(stream, content.length, "foto.jpg", "image/jpeg");
            List<UploadFile> files = List.of(file);
            List<String> urls = List.of("https://storage/foto.jpg");

            when(petRepository.create(input)).thenReturn(saved);
            when(fileStoragePort.uploadFiles(files, saved.getId())).thenReturn(urls);

            Pet result = interactor.execute(input, files);

            assertSame(saved, result);
            assertEquals("https://storage/foto.jpg", result.getPicture());
            verify(petRepository, times(1)).create(input);
            verify(fileStoragePort, times(1)).uploadFiles(files, saved.getId());
        }

        @Test
        @DisplayName("execute() debe propagar excepción si el repositorio falla")
        void execute_propagaExcepcionDeRepositorio()
        {
            Pet input = new Pet();
            List<UploadFile> files = List.of();
            when(petRepository.create(input)).thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> interactor.execute(input, files));
        }

        @Test
        @DisplayName("execute() debe propagar excepción si el storage falla")
        void execute_propagaExcepcionDeStorage()
        {
            Pet input = new Pet();
            Pet saved = new Pet();
            saved.setId(UUID.randomUUID());
            List<UploadFile> files = List.of();

            when(petRepository.create(input)).thenReturn(saved);
            when(fileStoragePort.uploadFiles(files, saved.getId()))
                    .thenThrow(new RuntimeException("Storage error"));

            assertThrows(RuntimeException.class, () -> interactor.execute(input, files));
        }
    }

    // ── GetPetsInteractorImpl ────────────────────────────────────────────

    @Nested
    @DisplayName("GetPetsInteractorImpl")
    class GetPetsInteractorImplTest
    {
        @Mock
        private PetRepository petRepository;

        @InjectMocks
        private GetPetsInteractorImpl interactor;

        @Test
        @DisplayName("execute() debe retornar la Page devuelta por el repositorio")
        void execute_retornaPaginaDelRepositorio()
        {
            Pet p1 = new Pet();
            p1.setName("Luna");
            Pet p2 = new Pet();
            p2.setName("Max");

            PageRequest pageRequest = new PageRequest(1, 10);
            Page<Pet> expectedPage = new Page<>(List.of(p1, p2), 1, 10, 2L);

            when(petRepository.getPets(pageRequest)).thenReturn(expectedPage);

            Page<Pet> result = interactor.execute(pageRequest);

            assertEquals(2, result.content().size());
            assertEquals(2L, result.totalElements());
            verify(petRepository, times(1)).getPets(pageRequest);
        }

        @Test
        @DisplayName("execute() debe retornar página vacía si no hay mascotas")
        void execute_paginaVacia()
        {
            PageRequest pageRequest = PageRequest.ofDefaults();
            Page<Pet> emptyPage = new Page<>(List.of(), 1, 200, 0L);

            when(petRepository.getPets(pageRequest)).thenReturn(emptyPage);

            Page<Pet> result = interactor.execute(pageRequest);

            assertTrue(result.content().isEmpty());
            assertEquals(0L, result.totalElements());
        }

        @Test
        @DisplayName("execute() con defaults debe delegar al repositorio")
        void execute_conDefaults_delegaAlRepositorio()
        {
            PageRequest defaults = PageRequest.ofDefaults();
            Page<Pet> page = new Page<>(List.of(), 1, 200, 0L);
            when(petRepository.getPets(defaults)).thenReturn(page);

            interactor.execute(defaults);

            verify(petRepository, times(1)).getPets(defaults);
        }
    }

    // ── UpdatePetInteractorImpl ──────────────────────────────────────────

    @Nested
    @DisplayName("UpdatePetInteractorImpl")
    class UpdatePetInteractorImplTest
    {
        @Mock
        private PetRepository petRepository;

        @InjectMocks
        private UpdatePetInteractorImpl interactor;

        @Test
        @DisplayName("execute() debe invocar petRepository.update() con la mascota recibida")
        void execute_invocaUpdate()
        {
            Pet pet = new Pet();
            pet.setId(UUID.randomUUID());

            interactor.execute(pet);

            verify(petRepository, times(1)).update(pet);
        }

        @Test
        @DisplayName("execute() debe propagar excepción si el repositorio falla")
        void execute_propagaExcepcion()
        {
            Pet pet = new Pet();
            doThrow(new RuntimeException("fallo update")).when(petRepository).update(pet);

            assertThrows(RuntimeException.class, () -> interactor.execute(pet));
        }
    }

    // ── DeletePetInteractorImpl ──────────────────────────────────────────

    @Nested
    @DisplayName("DeletePetInteractorImpl")
    class DeletePetInteractorImplTest
    {
        @Mock
        private PetRepository petRepository;

        @InjectMocks
        private DeletePetInteractorImpl interactor;

        @Test
        @DisplayName("execute() debe invocar petRepository.delete() con el id recibido")
        void execute_invocaDelete()
        {
            UUID id = UUID.randomUUID();

            interactor.execute(id);

            verify(petRepository, times(1)).delete(id);
        }

        @Test
        @DisplayName("execute() debe propagar excepción si el repositorio falla")
        void execute_propagaExcepcion()
        {
            UUID id = UUID.randomUUID();
            doThrow(new RuntimeException("no encontrado")).when(petRepository).delete(id);

            assertThrows(RuntimeException.class, () -> interactor.execute(id));
        }
    }
}
