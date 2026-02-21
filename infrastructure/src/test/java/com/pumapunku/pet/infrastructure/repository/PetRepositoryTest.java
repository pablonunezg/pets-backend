package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.exception.NotFoundException;
import com.pumapunku.pet.infrastructure.repository.entity.PetEntity;
import com.pumapunku.pet.infrastructure.repository.entity.Refuge;
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
 * Tests unitarios para {@link PetRepository} (adaptador de infraestructura).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PetRepository (infraestructura)")
class PetRepositoryTest
{
    @Mock
    private PetRepositoryJPA petRepositoryJPA;

    @Mock
    private RefugeRepositoryJPA refugeRepositoryJPA;

    @InjectMocks
    private PetRepository repository;

    private UUID petId;
    private UUID refugeId;
    private Pet domainPet;
    private PetEntity petEntity;
    private Refuge refuge;

    @BeforeEach
    void setUp()
    {
        petId    = UUID.randomUUID();
        refugeId = UUID.randomUUID();

        refuge = new Refuge();
        refuge.setId(refugeId);

        domainPet = new Pet();
        domainPet.setId(petId);
        domainPet.setName("Luna");
        domainPet.setRefugeId(refugeId);

        petEntity = new PetEntity();
        petEntity.setId(petId);
        petEntity.setName("Luna");
        petEntity.setRefuge(refuge);
    }

    // ── create ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("create()")
    class CreateTest
    {
        @Test
        @DisplayName("debe persistir y retornar la mascota cuando el refugio existe")
        void create_refugioExiste_retornaPet()
        {
            when(refugeRepositoryJPA.findById(refugeId)).thenReturn(Optional.of(refuge));
            when(petRepositoryJPA.save(any(PetEntity.class))).thenReturn(petEntity);

            Pet result = repository.create(domainPet);

            assertNotNull(result);
            assertEquals(petId, result.getId());
            verify(petRepositoryJPA, times(1)).save(any(PetEntity.class));
        }

        @Test
        @DisplayName("debe lanzar NotFoundException cuando el refugio no existe")
        void create_refugioNoExiste_lanzaNotFoundException()
        {
            when(refugeRepositoryJPA.findById(refugeId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> repository.create(domainPet));
            verify(petRepositoryJPA, never()).save(any());
        }
    }

    // ── update ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("update()")
    class UpdateTest
    {
        @Test
        @DisplayName("debe actualizar cuando la mascota existe")
        void update_mascotaExiste_invocaSave()
        {
            when(petRepositoryJPA.existsById(petId)).thenReturn(true);
            when(petRepositoryJPA.save(any(PetEntity.class))).thenReturn(petEntity);

            assertDoesNotThrow(() -> repository.update(domainPet));
            verify(petRepositoryJPA, times(1)).save(any(PetEntity.class));
        }

        @Test
        @DisplayName("debe lanzar NotFoundException cuando la mascota no existe")
        void update_mascotaNoExiste_lanzaNotFoundException()
        {
            when(petRepositoryJPA.existsById(petId)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> repository.update(domainPet));
            verify(petRepositoryJPA, never()).save(any());
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
        @DisplayName("debe retornar Page vacía si no hay mascotas")
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
        @DisplayName("debe convertir correctamente la página 1-based a 0-based para Spring Data")
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
