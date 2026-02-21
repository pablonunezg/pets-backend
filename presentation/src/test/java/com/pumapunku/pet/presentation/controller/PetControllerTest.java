package com.pumapunku.pet.presentation.controller;

import com.pumapunku.pet.application.CreatePetInteractor;
import com.pumapunku.pet.application.DeletePetInteractor;
import com.pumapunku.pet.application.GetPetsInteractor;
import com.pumapunku.pet.application.UpdatePetInteractor;
import com.pumapunku.pet.domain.AgeGroup;
import com.pumapunku.pet.domain.EnergyLevel;
import com.pumapunku.pet.domain.Gender;
import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.Size;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para {@link PetController} usando MockMvc standalone.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PetController")
class PetControllerTest
{
    @Mock private SupabaseStorageService supabaseStorageService;
    @Mock private GetPetsInteractor getPetsInteractor;
    @Mock private CreatePetInteractor createPetInteractor;
    @Mock private UpdatePetInteractor updatePetInteractor;
    @Mock private DeletePetInteractor deletePetInteractor;

    @InjectMocks
    private PetController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp()
    {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new PetApiExceptionHandler())
                .build();
    }

    // ── GET /pet ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /pet")
    class GetPetsTest
    {
        @Test
        @DisplayName("debe retornar 200 con array de mascotas y header X-Total-Count")
        void getPets_retorna200ConArrayYHeader() throws Exception
        {
            Pet pet = new Pet();
            pet.setId(UUID.randomUUID());
            pet.setName("Luna");

            Page<Pet> page = new Page<>(List.of(pet), 1, 200, 1L);
            when(getPetsInteractor.execute(any(PageRequest.class))).thenReturn(page);

            mockMvc.perform(get("/pet"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(ResponseUtils.HEADER_TOTAL_COUNT, "1"))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].name").value("Luna"));
        }

        @Test
        @DisplayName("debe retornar array vacío con X-Total-Count 0 si no hay mascotas")
        void getPets_sinMascotas_retornaArrayVacioYHeaderCero() throws Exception
        {
            Page<Pet> emptyPage = new Page<>(List.of(), 1, 200, 0L);
            when(getPetsInteractor.execute(any(PageRequest.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/pet"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(ResponseUtils.HEADER_TOTAL_COUNT, "0"))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("debe usar pageNumber y pageSize del query param si se envían")
        void getPets_conParamsExplicitos_delegaAlInteractor() throws Exception
        {
            Page<Pet> page = new Page<>(List.of(), 2, 10, 0L);
            when(getPetsInteractor.execute(any(PageRequest.class))).thenReturn(page);

            mockMvc.perform(get("/pet")
                            .param("pageNumber", "2")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(ResponseUtils.HEADER_TOTAL_COUNT, "0"));
        }

        @Test
        @DisplayName("debe usar defaults (page=1, size=200) si no se envían params")
        void getPets_sinParams_usaDefaults() throws Exception
        {
            Page<Pet> page = new Page<>(List.of(), 1, 200, 5L);
            when(getPetsInteractor.execute(any(PageRequest.class))).thenReturn(page);

            mockMvc.perform(get("/pet"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(ResponseUtils.HEADER_TOTAL_COUNT, "5"));
        }
    }

    // ── PUT /pet/{id} ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /pet/{petId}")
    class UpdatePetTest
    {
        @Test
        @DisplayName("debe retornar 204 al actualizar correctamente")
        void update_retorna204() throws Exception
        {
            UUID id = UUID.randomUUID();
            PetRequest request = buildPetRequest();

            doNothing().when(updatePetInteractor).execute(any(Pet.class));

            mockMvc.perform(put("/pet/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            verify(updatePetInteractor, times(1)).execute(any(Pet.class));
        }

        @Test
        @DisplayName("debe retornar 404 si la mascota no existe")
        void update_mascotaNoExiste_retorna404() throws Exception
        {
            UUID id = UUID.randomUUID();
            PetRequest request = buildPetRequest();
            doThrow(new NotFoundException("Pet", id.toString()))
                    .when(updatePetInteractor).execute(any(Pet.class));

            mockMvc.perform(put("/pet/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    // ── DELETE /pet/{id} ──────────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /pet/{petId}")
    class DeletePetTest
    {
        @Test
        @DisplayName("debe retornar 204 al eliminar correctamente")
        void delete_retorna204() throws Exception
        {
            UUID id = UUID.randomUUID();
            doNothing().when(deletePetInteractor).execute(id);

            mockMvc.perform(delete("/pet/" + id))
                    .andExpect(status().isNoContent());

            verify(deletePetInteractor, times(1)).execute(id);
        }

        @Test
        @DisplayName("debe retornar 404 si la mascota no existe")
        void delete_mascotaNoExiste_retorna404() throws Exception
        {
            UUID id = UUID.randomUUID();
            doThrow(new NotFoundException("Pet", id.toString()))
                    .when(deletePetInteractor).execute(id);

            mockMvc.perform(delete("/pet/" + id))
                    .andExpect(status().isNotFound());
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private PetRequest buildPetRequest()
    {
        PetRequest r = new PetRequest();
        r.setName("Luna");
        r.setPicture("img.jpg");
        r.setBreed("Labrador");
        r.setAgeGroup(AgeGroup.ADULT);
        r.setSize(Size.MEDIUM);
        r.setGender(Gender.FEMALE);
        r.setEnergyLevel(EnergyLevel.HIGH);
        r.setRefugeId(UUID.randomUUID());
        return r;
    }
}
