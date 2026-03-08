package com.pumapunku.pet.presentation.controller;

import com.pumapunku.pet.application.*;
import com.pumapunku.pet.application.port.UploadFile;
import com.pumapunku.pet.domain.*;
import com.pumapunku.pet.domain.exception.NotFoundException;
import com.pumapunku.pet.presentation.PetApiExceptionHandler;
import com.pumapunku.pet.presentation.request.PetRequest;
import com.pumapunku.pet.presentation.service.SupabaseStorageService;
import com.pumapunku.pet.presentation.util.ResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetController")
class PetControllerTest
{
    @Mock
    private SupabaseStorageService supabaseStorageService;
    @Mock
    private GetPetsInteractor getPetsInteractor;
    @Mock
    private GetPetByIdInteractor getPetByIdInteractor;
    @Mock
    private CreatePetInteractor createPetInteractor;
    @Mock
    private UpdatePetInteractor updatePetInteractor;
    @Mock
    private DeletePetInteractor deletePetInteractor;

    @InjectMocks
    private PetController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp()
    {
        DefaultFormattingConversionService cs = new DefaultFormattingConversionService();
        cs.addConverter(String.class, PetRequest.class, json ->
        {
            try
            {
                return objectMapper.readValue(json, PetRequest.class);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        });

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new PetApiExceptionHandler())
                .setConversionService(cs)
                .build();
    }

    // ── GET /pet ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /pet")
    class GetPetsTest
    {
        @Test
        @DisplayName("debe retornar 200 con array de mascotas y header X-Total-Count")
        void getPets_retorna200() throws Exception
        {
            Pet pet = new Pet();
            pet.setId(UUID.randomUUID());
            pet.setName("Luna");

            Page<Pet> page = new Page<>(List.of(pet), 1, 200, 1L);
            when(getPetsInteractor.execute(any(PageRequest.class))).thenReturn(page);

            mockMvc.perform(get("/pet"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(ResponseUtils.HEADER_TOTAL_COUNT, "1"))
                    .andExpect(jsonPath("$[0].name").value("Luna"));
        }

        @Test
        @DisplayName("should return empty array with X-Total-Count 0")
        void getPets_vacio() throws Exception
        {
            when(getPetsInteractor.execute(any(PageRequest.class)))
                    .thenReturn(new Page<>(List.of(), 1, 200, 0L));

            mockMvc.perform(get("/pet"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(ResponseUtils.HEADER_TOTAL_COUNT, "0"))
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    // ── PUT /pet/{petId} ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /pet/{petId}")
    class UpdatePetTest
    {
        @Test
        @DisplayName("con archivos: debe delegar al interactor con la lista de UploadFiles")
        void update_conArchivos_delegaAlInteractorConFiles() throws Exception
        {
            UUID petId = UUID.randomUUID();
            doNothing().when(updatePetInteractor).execute(any(Pet.class), any());

            MockMultipartFile filePart = new MockMultipartFile(
                    "files", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, "img-bytes".getBytes());

            mockMvc.perform(multipart(HttpMethod.PUT, "/pet/" + petId)
                            .file(filePart)
                            .param("pet", objectMapper.writeValueAsString(buildPetRequest())))
                    .andExpect(status().isNoContent());

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<UploadFile>> filesCaptor = ArgumentCaptor.forClass(List.class);
            verify(updatePetInteractor).execute(any(Pet.class), filesCaptor.capture());
            assertFalse(filesCaptor.getValue().isEmpty(),
                    "The interactor should receive the non-empty file list");

            // The controller must not touch the bucket directly
            verify(supabaseStorageService, never()).deleteFiles(anyString());
            verify(supabaseStorageService, never()).uploadFiles(any(), any());
        }

        @Test
        @DisplayName("without files: should delegate to the interactor with empty list")
        void update_sinArchivos_delegaAlInteractorConListaVacia() throws Exception
        {
            UUID petId = UUID.randomUUID();
            doNothing().when(updatePetInteractor).execute(any(Pet.class), any());

            mockMvc.perform(multipart(HttpMethod.PUT, "/pet/" + petId)
                            .param("pet", objectMapper.writeValueAsString(buildPetRequest())))
                    .andExpect(status().isNoContent());

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<UploadFile>> filesCaptor = ArgumentCaptor.forClass(List.class);
            verify(updatePetInteractor).execute(any(Pet.class), filesCaptor.capture());
            assertTrue(filesCaptor.getValue().isEmpty(),
                    "The interactor should receive an empty list when there are no files");

            verify(supabaseStorageService, never()).deleteFiles(anyString());
            verify(supabaseStorageService, never()).uploadFiles(any(), any());
        }

        @Test
        @DisplayName("debe propagar 404 cuando el interactor lanza NotFoundException")
        void update_interactorLanzaNotFound_retorna404() throws Exception
        {
            UUID petId = UUID.randomUUID();
            doThrow(new NotFoundException("Pet", petId.toString()))
                    .when(updatePetInteractor).execute(any(Pet.class), any());

            mockMvc.perform(multipart(HttpMethod.PUT, "/pet/" + petId)
                            .param("pet", objectMapper.writeValueAsString(buildPetRequest())))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.details").isArray());
        }

        @Test
        @DisplayName("debe pasar el petId correcto a la entidad Pet")
        void update_asignaPetIdCorrectamenteALaEntidad() throws Exception
        {
            UUID petId = UUID.randomUUID();
            doNothing().when(updatePetInteractor).execute(any(Pet.class), any());

            mockMvc.perform(multipart(HttpMethod.PUT, "/pet/" + petId)
                            .param("pet", objectMapper.writeValueAsString(buildPetRequest())))
                    .andExpect(status().isNoContent());

            ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
            verify(updatePetInteractor).execute(petCaptor.capture(), any());
            assertEquals(petId, petCaptor.getValue().getId());
        }
    }

    // ── DELETE /pet/{petId} ───────────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /pet/{petId}")
    class DeletePetTest
    {
        @Test
        @DisplayName("debe retornar 204 y borrar archivos del bucket")
        void delete_retorna204YBorraArchivos() throws Exception
        {
            UUID id = UUID.randomUUID();
            Pet pet = new Pet();
            pet.setId(id);
            pet.setPicture("https://storage/public/pets/uuid_foto.jpg");

            when(getPetByIdInteractor.execute(id)).thenReturn(pet);
            doNothing().when(supabaseStorageService).deleteFiles(anyString());
            doNothing().when(deletePetInteractor).execute(id);

            mockMvc.perform(delete("/pet/" + id))
                    .andExpect(status().isNoContent());

            verify(getPetByIdInteractor).execute(id);
            verify(supabaseStorageService).deleteFiles(pet.getPicture());
            verify(deletePetInteractor).execute(id);
        }

        @Test
        @DisplayName("debe retornar 404 si la mascota no existe")
        void delete_noExiste_retorna404() throws Exception
        {
            UUID id = UUID.randomUUID();
            doThrow(new NotFoundException("Pet", id.toString()))
                    .when(getPetByIdInteractor).execute(id);

            mockMvc.perform(delete("/pet/" + id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"));

            verify(deletePetInteractor, never()).execute(any());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private PetRequest buildPetRequest()
    {
        PetRequest r = new PetRequest();
        r.setName("Luna");
        r.setDescription("Perrita amigable y juguetona");
        r.setBreed("Labrador");
        r.setAgeGroup(AgeGroup.ADULT);
        r.setPetSize(PetSize.MEDIUM);
        r.setGender(Gender.FEMALE);
        r.setEnergyLevel(EnergyLevel.HIGH);
        r.setStatus(Status.FOR_ADOPTION);
        return r;
    }
}