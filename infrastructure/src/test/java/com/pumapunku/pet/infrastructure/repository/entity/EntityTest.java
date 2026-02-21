package com.pumapunku.pet.infrastructure.repository.entity;

import com.pumapunku.pet.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para las entidades JPA del módulo de infraestructura.
 */
@DisplayName("Entidades JPA de infraestructura")
class EntityTest
{
    // ── PetEntity ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PetEntity")
    class PetEntityTest
    {
        @Test
        @DisplayName("prePersist() debe asignar id cuando es null")
        void prePersist_idEsNull_asignaId()
        {
            PetEntity entity = new PetEntity();
            assertNull(entity.getId());

            entity.prePersist();

            assertNotNull(entity.getId());
        }

        @Test
        @DisplayName("prePersist() debe asignar createdAt cuando es null")
        void prePersist_createdAtEsNull_asignaFecha()
        {
            PetEntity entity = new PetEntity();
            entity.prePersist();

            assertNotNull(entity.getCreatedAt());
        }

        @Test
        @DisplayName("prePersist() NO debe sobreescribir un id ya existente")
        void prePersist_idYaExiste_noSobreescribe()
        {
            PetEntity entity = new PetEntity();
            UUID existing = UUID.randomUUID();
            entity.setId(existing);

            entity.prePersist();

            assertEquals(existing, entity.getId());
        }

        @Test
        @DisplayName("prePersist() NO debe sobreescribir un createdAt ya existente")
        void prePersist_createdAtYaExiste_noSobreescribe()
        {
            PetEntity entity = new PetEntity();
            LocalDateTime existing = LocalDateTime.of(2024, 1, 1, 0, 0);
            entity.setCreatedAt(existing);

            entity.prePersist();

            assertEquals(existing, entity.getCreatedAt());
        }

        @Test
        @DisplayName("getters y setters deben funcionar correctamente")
        void gettersSetters()
        {
            PetEntity entity = new PetEntity();
            entity.setName("Luna");
            entity.setBreed("Labrador");
            entity.setNeutered(true);
            entity.setGoodWithDogs(false);
            entity.setGoodWithKids(true);

            assertEquals("Luna", entity.getName());
            assertEquals("Labrador", entity.getBreed());
            assertTrue(entity.isNeutered());
            assertFalse(entity.isGoodWithDogs());
            assertTrue(entity.isGoodWithKids());
        }
    }

    // ── Refuge ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Refuge")
    class RefugeTest
    {
        @Test
        @DisplayName("generateId() debe asignar id cuando es null")
        void generateId_idEsNull_asignaId()
        {
            Refuge refuge = new Refuge();
            assertNull(refuge.getId());

            refuge.generateId();

            assertNotNull(refuge.getId());
        }

        @Test
        @DisplayName("generateId() NO debe sobreescribir un id existente")
        void generateId_idYaExiste_noSobreescribe()
        {
            Refuge refuge = new Refuge();
            UUID existing = UUID.randomUUID();
            refuge.setId(existing);

            refuge.generateId();

            assertEquals(existing, refuge.getId());
        }

        @Test
        @DisplayName("addPet() debe agregar la mascota y establecer la referencia al refugio")
        void addPet_agregaMascotaYAsignaRefugio()
        {
            Refuge refuge = new Refuge();
            PetEntity pet = new PetEntity();

            refuge.addPet(pet);

            assertTrue(refuge.getPets().contains(pet));
            assertSame(refuge, pet.getRefuge());
        }

        @Test
        @DisplayName("removePet() debe quitar la mascota y eliminar la referencia al refugio")
        void removePet_quitaMascotaYNulifcaRefugio()
        {
            Refuge refuge = new Refuge();
            PetEntity pet = new PetEntity();
            refuge.addPet(pet);

            refuge.removePet(pet);

            assertFalse(refuge.getPets().contains(pet));
            assertNull(pet.getRefuge());
        }

        @Test
        @DisplayName("lista de mascotas inicia vacía")
        void listaMascotas_iniciaVacia()
        {
            assertTrue(new Refuge().getPets().isEmpty());
        }
    }

    // ── User ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("User (entidad)")
    class UserEntityTest
    {
        @Test
        @DisplayName("builder debe construir la entidad con todos los campos")
        void builder_construyeEntidad()
        {
            UUID id = UUID.randomUUID();
            User user = User.builder()
                    .id(id)
                    .username("alice")
                    .password("hashed")
                    .role(Role.ADMIN)
                    .build();

            assertEquals(id, user.getId());
            assertEquals("alice", user.getUsername());
            assertEquals("hashed", user.getPassword());
            assertEquals(Role.ADMIN, user.getRole());
        }

        @Test
        @DisplayName("constructor sin argumentos debe crear objeto vacío")
        void constructorVacio_objetoVacio()
        {
            User user = new User();
            assertNull(user.getId());
            assertNull(user.getUsername());
        }
    }
}
