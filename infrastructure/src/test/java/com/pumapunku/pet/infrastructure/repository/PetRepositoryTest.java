package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.exception.NotFoundException;
import com.pumapunku.pet.infrastructure.repository.entity.PetEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PetRepository} (infrastructure adapter).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PetRepository (infraestructura)")
class PetRepositoryTest
{
    @Mock
    private PetRepositoryJPA petRepositoryJPA;

    @InjectMocks
    private PetRepository repository;

    private UUID petId;
    private Pet domainPet;
    private PetEntity petEntity;

    @BeforeEach
    void setUp()
    {
        petId = UUID.randomUUID();

        domainPet = new Pet();
        domainPet.setId(petId);
        domainPet.setName("Luna");

        petEntity = new PetEntity();
        petEntity.setId(petId);
        petEntity.setName("Luna");
    }

    // ── create ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("create()")
    class CreateTest
    {
        @Test
        @DisplayName("debe persistir y retornar la mascota con id y nombre mapeados")
        void create_retornaPetMapeado()
        {
            when(petRepositoryJPA.save(any(PetEntity.class))).thenReturn(petEntity);

            Pet result = repository.create(domainPet);

            assertNotNull(result);
            assertEquals(petId, result.getId());
            assertEquals("Luna", result.getName());
            verify(petRepositoryJPA, times(1)).save(any(PetEntity.class));
        }

        @Test
        @DisplayName("should propagate exception if JPA fails to save")
        void create_jpaFalla_propagaExcepcion()
        {
            when(petRepositoryJPA.save(any(PetEntity.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> repository.create(domainPet));
        }
    }

    // ── update ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("update()")
    class UpdateTest
    {
        /**
         * The real implementation uses {@code findById().orElseThrow()}, not {@code existsById}.
         * Previous tests stubbed {@code existsById} (which is never called),
         * causing {@code UnnecessaryStubbingException} with strict Mockito, and the first
         * test failed because {@code findById} returned {@code Optional.empty()} by default
         * throwing {@code NotFoundException} instead of completing without error.
         */
        @Test
        @DisplayName("debe cargar la entidad existente, aplicar cambios en-sitio y llamar a save con esa misma entidad")
        void update_mascotaExiste_cargaActualizaYGuarda()
        {
            when(petRepositoryJPA.findById(petId)).thenReturn(Optional.of(petEntity));
            when(petRepositoryJPA.save(petEntity)).thenReturn(petEntity);

            assertDoesNotThrow(() -> repository.update(domainPet));

            // save must receive the EXISTING entity (not a new one), since update works in-place
            verify(petRepositoryJPA, times(1)).findById(petId);
            verify(petRepositoryJPA, times(1)).save(petEntity);
        }

        @Test
        @DisplayName("should throw NotFoundException when findById returns empty")
        void update_mascotaNoExiste_lanzaNotFoundException()
        {
            when(petRepositoryJPA.findById(petId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> repository.update(domainPet));
            verify(petRepositoryJPA, never()).save(any());
        }

        @Test
        @DisplayName("should propagate exception if JPA fails to save")
        void update_jpaFallaAlGuardar_propagaExcepcion()
        {
            when(petRepositoryJPA.findById(petId)).thenReturn(Optional.of(petEntity));
            when(petRepositoryJPA.save(petEntity)).thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> repository.update(domainPet));
        }
    }

    // ── delete ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("delete()")
    class DeleteTest
    {
        @Test
        @DisplayName("debe eliminar cuando la mascota existe")
        void delete_mascotaExiste_invocaDeleteById()
        {
            when(petRepositoryJPA.existsById(petId)).thenReturn(true);

            assertDoesNotThrow(() -> repository.delete(petId));
            verify(petRepositoryJPA, times(1)).deleteById(petId);
        }

        @Test
        @DisplayName("debe lanzar NotFoundException cuando la mascota no existe")
        void delete_mascotaNoExiste_lanzaNotFoundException()
        {
            when(petRepositoryJPA.existsById(petId)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> repository.delete(petId));
            verify(petRepositoryJPA, never()).deleteById(any());
        }
    }

    // ── findById ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById()")
    class FindByIdTest
    {
        @Test
        @DisplayName("debe retornar Optional con mascota mapeada cuando existe")
        void findById_existente_retornaOptionalConPetMapeado()
        {
            when(petRepositoryJPA.findById(petId)).thenReturn(Optional.of(petEntity));

            Optional<Pet> result = repository.findById(petId);

            assertTrue(result.isPresent());
            assertEquals(petId, result.get().getId());
            assertEquals("Luna", result.get().getName());
        }

        @Test
        @DisplayName("should return empty Optional when not found")
        void findById_noExiste_retornaVacio()
        {
            when(petRepositoryJPA.findById(petId)).thenReturn(Optional.empty());

            Optional<Pet> result = repository.findById(petId);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should propagate exception if JPA fails")
        void findById_jpaFalla_propagaExcepcion()
        {
            when(petRepositoryJPA.findById(petId)).thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> repository.findById(petId));
        }
    }

    // ── getPets(PageRequest) ─────────────────────────────────────────────

    @Nested
    @DisplayName("getPets(PageRequest)")
    class GetPetsTest
    {
        @Test
        @DisplayName("debe retornar Page con mascotas mapeadas al dominio")
        void getPets_retornaPaginaMapeada()
        {
            PageRequest pageRequest = new PageRequest(1, 10);
            org.springframework.data.domain.Page<PetEntity> springPage =
                    new PageImpl<>(List.of(petEntity),
                            org.springframework.data.domain.PageRequest.of(0, 10), 1L);

            when(petRepositoryJPA.findAll(any(org.springframework.data.domain.Pageable.class)))
                    .thenReturn(springPage);

            Page<Pet> result = repository.getPets(pageRequest);

            assertEquals(1, result.content().size());
            assertEquals(petId, result.content().get(0).getId());
            assertEquals(1L, result.totalElements());
            assertEquals(1, result.pageNumber());
            assertEquals(10, result.pageSize());
        }

        @Test
        @DisplayName("should return empty Page when there are no pets")
        void getPets_sinMascotas_retornaPaginaVacia()
        {
            PageRequest pageRequest = PageRequest.ofDefaults();
            org.springframework.data.domain.Page<PetEntity> springPage =
                    new PageImpl<>(List.of(),
                            org.springframework.data.domain.PageRequest.of(0, 200), 0L);

            when(petRepositoryJPA.findAll(any(org.springframework.data.domain.Pageable.class)))
                    .thenReturn(springPage);

            Page<Pet> result = repository.getPets(pageRequest);

            assertTrue(result.content().isEmpty());
            assertEquals(0L, result.totalElements());
        }

        @Test
        @DisplayName("should correctly convert 1-based page to 0-based for Spring Data")
        void getPets_convierteCorrectamentePagina()
        {
            PageRequest pageRequest = new PageRequest(2, 5);
            org.springframework.data.domain.Page<PetEntity> springPage =
                    new PageImpl<>(List.of(),
                            org.springframework.data.domain.PageRequest.of(1, 5), 0L);

            when(petRepositoryJPA.findAll(any(org.springframework.data.domain.Pageable.class)))
                    .thenReturn(springPage);

            repository.getPets(pageRequest);

            verify(petRepositoryJPA, times(1)).findAll(
                    org.springframework.data.domain.PageRequest.of(1, 5)
            );
        }
    }
}