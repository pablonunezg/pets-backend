package com.pumapunku.pet.application.impl;

import com.pumapunku.pet.application.port.FileStoragePort;
import com.pumapunku.pet.application.port.UploadFile;
import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.exception.NotFoundException;
import com.pumapunku.pet.domain.repository.PetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the application layer interactors.
 *
 * <p>Each nested class covers one interactor implementation in isolation,
 * using Mockito to replace all dependencies.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Application layer interactors")
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

        private static final String BASE_URL = "https://storage/";

        /**
         * Main path: picture is built as {@code "baseUrl" + baseUrl + "," + originalFilename},
         * the pet is persisted, and then the files are uploaded using the saved pet's id.
         * The returned object must be exactly what the repository returns.
         */
        @Test
        @DisplayName("execute() sets picture from baseUrl + filename, persists pet, uploads files, and returns saved pet")
        void execute_singleFile_setsPictureAndReturnsSavedPet()
        {
            Pet input = new Pet();
            input.setName("Luna");

            Pet saved = new Pet();
            saved.setId(UUID.randomUUID());
            saved.setName("Luna");

            byte[] content = {1};
            UploadFile file = new UploadFile(new ByteArrayInputStream(content), content.length, "foto.jpg", "image/jpeg");
            List<UploadFile> files = List.of(file);

            when(fileStoragePort.getBaseUrl()).thenReturn(BASE_URL);
            when(petRepository.create(input)).thenReturn(saved);

            Pet result = interactor.execute(input, files);

            assertSame(saved, result);

            ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository, times(1)).create(captor.capture());
            assertEquals("baseUrl" + BASE_URL + ",foto.jpg", captor.getValue().getPicture());

            verify(fileStoragePort, times(1)).uploadFiles(files, saved.getId());
        }

        /**
         * With multiple files, filenames are joined with comma after the "baseUrl" prefix.
         */
        @Test
        @DisplayName("execute() with multiple files joins filenames with comma after the baseUrl prefix")
        void execute_multipleFiles_joinedFilenamesInPicture()
        {
            Pet input = new Pet();
            input.setName("Max");

            Pet saved = new Pet();
            saved.setId(UUID.randomUUID());

            List<UploadFile> files = List.of(
                    new UploadFile(new ByteArrayInputStream(new byte[]{1}), 1, "a.jpg", "image/jpeg"),
                    new UploadFile(new ByteArrayInputStream(new byte[]{2}), 1, "b.jpg", "image/jpeg")
            );

            when(fileStoragePort.getBaseUrl()).thenReturn(BASE_URL);
            when(petRepository.create(input)).thenReturn(saved);

            interactor.execute(input, files);

            ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository).create(captor.capture());
            assertEquals("baseUrl" + BASE_URL + ",a.jpg,b.jpg", captor.getValue().getPicture());
        }

        /**
         * With an empty file list, filenames join produces an empty string,
         * resulting in picture = "baseUrl{baseUrl},". Repository and uploadFiles are still called.
         */
        @Test
        @DisplayName("execute() with empty file list sets picture to 'baseUrl<baseUrl>,' and still calls create and uploadFiles")
        void execute_emptyFileList_pictureWithBaseUrlOnly()
        {
            Pet input = new Pet();
            Pet saved = new Pet();
            saved.setId(UUID.randomUUID());
            List<UploadFile> files = List.of();

            when(fileStoragePort.getBaseUrl()).thenReturn(BASE_URL);
            when(petRepository.create(input)).thenReturn(saved);

            Pet result = interactor.execute(input, files);

            assertSame(saved, result);

            ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository).create(captor.capture());
            assertEquals("baseUrl" + BASE_URL + ",", captor.getValue().getPicture());

            verify(fileStoragePort, times(1)).uploadFiles(files, saved.getId());
        }

        /**
         * If {@code getBaseUrl()} throws, execution stops immediately:
         * {@code petRepository.create()} must never be called.
         */
        @Test
        @DisplayName("execute() propagates exception from getBaseUrl() and never calls create")
        void execute_getBaseUrlThrows_propagatesExceptionWithoutCallingCreate()
        {
            Pet input = new Pet();
            List<UploadFile> files = List.of(
                    new UploadFile(new ByteArrayInputStream(new byte[]{1}), 1, "f.jpg", "image/jpeg"));

            when(fileStoragePort.getBaseUrl()).thenThrow(new RuntimeException("Storage error"));

            assertThrows(RuntimeException.class, () -> interactor.execute(input, files));
            verify(petRepository, never()).create(any());
        }

        /**
         * If {@code petRepository.create()} throws, uploadFiles is never reached
         * because it is called after create.
         */
        @Test
        @DisplayName("execute() propagates exception from create() and never calls uploadFiles")
        void execute_createThrows_propagatesExceptionWithoutCallingUpload()
        {
            Pet input = new Pet();
            List<UploadFile> files = List.of();

            when(fileStoragePort.getBaseUrl()).thenReturn(BASE_URL);
            when(petRepository.create(input)).thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> interactor.execute(input, files));
            verify(fileStoragePort, never()).uploadFiles(any(), any());
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
        @DisplayName("execute() returns the Page returned by the repository")
        void execute_returnsPageFromRepository()
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
        @DisplayName("execute() returns an empty page when there are no pets")
        void execute_emptyPage()
        {
            PageRequest pageRequest = PageRequest.ofDefaults();
            Page<Pet> emptyPage = new Page<>(List.of(), 1, 200, 0L);

            when(petRepository.getPets(pageRequest)).thenReturn(emptyPage);

            Page<Pet> result = interactor.execute(pageRequest);

            assertTrue(result.content().isEmpty());
            assertEquals(0L, result.totalElements());
        }
    }

    // ── GetPetByIdInteractorImpl ─────────────────────────────────────────

    @Nested
    @DisplayName("GetPetByIdInteractorImpl")
    class GetPetByIdInteractorImplTest
    {

        @Mock
        private PetRepository petRepository;

        @InjectMocks
        private GetPetByIdInteractorImpl interactor;

        @Test
        @DisplayName("execute() returns the pet when it exists")
        void execute_petExists_returnsPet()
        {
            UUID id = UUID.randomUUID();
            Pet pet = new Pet();
            pet.setId(id);
            pet.setName("Luna");

            when(petRepository.findById(id)).thenReturn(Optional.of(pet));

            Pet result = interactor.execute(id);

            assertSame(pet, result);
            verify(petRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("execute() throws NotFoundException when the pet does not exist")
        void execute_petNotFound_throwsNotFoundException()
        {
            UUID id = UUID.randomUUID();
            when(petRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> interactor.execute(id));
        }
    }

    // ── UpdatePetInteractorImpl ──────────────────────────────────────────

    @Nested
    @DisplayName("UpdatePetInteractorImpl")
    class UpdatePetInteractorImplTest
    {

        @Mock
        private PetRepository petRepository;

        @Mock
        private FileStoragePort fileStoragePort;

        @InjectMocks
        private UpdatePetInteractorImpl interactor;

        @Test
        @DisplayName("with files: deletes old ones, uploads new ones using pet id, and updates picture")
        void execute_withFiles_replacesImagesAndUpdates()
        {
            UUID id = UUID.randomUUID();
            String oldPicture = "https://storage/old.jpg";
            String newPicture = "https://storage/new.jpg";

            Pet incoming = new Pet();
            incoming.setId(id);
            incoming.setName("Luna");

            Pet existing = new Pet();
            existing.setId(id);
            existing.setPicture(oldPicture);

            UploadFile file = new UploadFile(
                    new ByteArrayInputStream(new byte[]{1}), 1, "new.jpg", "image/jpeg");
            List<UploadFile> files = List.of(file);

            when(petRepository.findById(id)).thenReturn(Optional.of(existing));
            when(fileStoragePort.uploadFiles(files, id)).thenReturn(List.of(newPicture));

            interactor.execute(incoming, files);

            verify(fileStoragePort).deleteFiles(oldPicture);
            verify(fileStoragePort).uploadFiles(files, id);

            ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository).update(captor.capture());
            assertEquals(newPicture, captor.getValue().getPicture());
        }

        @Test
        @DisplayName("with multiple files: picture contains URLs joined with comma")
        void execute_withMultipleFiles_pictureJoinedWithComma()
        {
            UUID id = UUID.randomUUID();
            Pet incoming = new Pet();
            incoming.setId(id);

            Pet existing = new Pet();
            existing.setId(id);
            existing.setPicture("https://storage/old.jpg");

            List<UploadFile> files = List.of(
                    new UploadFile(new ByteArrayInputStream(new byte[]{1}), 1, "a.jpg", "image/jpeg"),
                    new UploadFile(new ByteArrayInputStream(new byte[]{2}), 1, "b.jpg", "image/jpeg")
            );
            List<String> urls = List.of("https://storage/a.jpg", "https://storage/b.jpg");

            when(petRepository.findById(id)).thenReturn(Optional.of(existing));
            when(fileStoragePort.uploadFiles(files, id)).thenReturn(urls);

            interactor.execute(incoming, files);

            ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository).update(captor.capture());
            assertEquals("https://storage/a.jpg,https://storage/b.jpg", captor.getValue().getPicture());
        }

        @Test
        @DisplayName("without files (empty list): keeps existing picture and does not touch the bucket")
        void execute_emptyFileList_keepsExistingPicture()
        {
            UUID id = UUID.randomUUID();
            String existingPicture = "https://storage/existing.jpg";

            Pet incoming = new Pet();
            incoming.setId(id);
            incoming.setName("Luna");

            Pet existing = new Pet();
            existing.setId(id);
            existing.setPicture(existingPicture);

            when(petRepository.findById(id)).thenReturn(Optional.of(existing));

            interactor.execute(incoming, List.of());

            verify(fileStoragePort, never()).deleteFiles(anyString());
            verify(fileStoragePort, never()).uploadFiles(any(), any());

            ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository).update(captor.capture());
            assertEquals(existingPicture, captor.getValue().getPicture());
        }

        @Test
        @DisplayName("without files (null): keeps existing picture and does not touch the bucket")
        void execute_nullFiles_keepsExistingPicture()
        {
            UUID id = UUID.randomUUID();
            String existingPicture = "https://storage/existing.jpg";

            Pet incoming = new Pet();
            incoming.setId(id);

            Pet existing = new Pet();
            existing.setId(id);
            existing.setPicture(existingPicture);

            when(petRepository.findById(id)).thenReturn(Optional.of(existing));

            interactor.execute(incoming, null);

            verify(fileStoragePort, never()).deleteFiles(anyString());
            verify(fileStoragePort, never()).uploadFiles(any(), any());

            ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository).update(captor.capture());
            assertEquals(existingPicture, captor.getValue().getPicture());
        }

        @Test
        @DisplayName("throws NotFoundException if the pet does not exist")
        void execute_petNotFound_throwsNotFoundException()
        {
            UUID id = UUID.randomUUID();
            Pet incoming = new Pet();
            incoming.setId(id);

            when(petRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> interactor.execute(incoming, List.of()));

            verify(petRepository, never()).update(any());
            verify(fileStoragePort, never()).deleteFiles(anyString());
        }

        @Test
        @DisplayName("propagates exception if storage fails while uploading")
        void execute_uploadThrows_propagatesException()
        {
            UUID id = UUID.randomUUID();
            Pet incoming = new Pet();
            incoming.setId(id);

            Pet existing = new Pet();
            existing.setId(id);
            existing.setPicture("https://storage/old.jpg");

            List<UploadFile> files = List.of(
                    new UploadFile(new ByteArrayInputStream(new byte[]{1}), 1, "f.jpg", "image/jpeg"));

            when(petRepository.findById(id)).thenReturn(Optional.of(existing));
            when(fileStoragePort.uploadFiles(files, id)).thenThrow(new RuntimeException("Storage error"));

            assertThrows(RuntimeException.class, () -> interactor.execute(incoming, files));
            verify(petRepository, never()).update(any());
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
        @DisplayName("execute() invokes petRepository.delete() with the received id")
        void execute_callsDelete()
        {
            UUID id = UUID.randomUUID();

            interactor.execute(id);

            verify(petRepository, times(1)).delete(id);
        }

        @Test
        @DisplayName("execute() propagates exception if the repository throws")
        void execute_repositoryThrows_propagatesException()
        {
            UUID id = UUID.randomUUID();
            doThrow(new RuntimeException("not found")).when(petRepository).delete(id);

            assertThrows(RuntimeException.class, () -> interactor.execute(id));
        }
    }
}