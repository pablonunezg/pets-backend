package com.pumapunku.pet.infrastructure.repository.entity;

import com.pumapunku.pet.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entidades JPA de infraestructura")
class EntityTest
{
    @Nested
    @DisplayName("PetEntity")
    class PetEntityTest
    {
        @Test
        @DisplayName("prePersist() debe asignar id cuando es null")
        void prePersist_idEsNull_asignaId()
        {
            PetEntity entity = new PetEntity();
            entity.setPicture("photo.jpg");
            assertNull(entity.getId());
            entity.prePersist();
            assertNotNull(entity.getId());
        }

        @Test
        @DisplayName("prePersist() debe asignar createdAt cuando es null")
        void prePersist_createdAtEsNull_asignaFecha()
        {
            PetEntity entity = new PetEntity();
            entity.setPicture("photo.jpg");
            entity.prePersist();
            assertNotNull(entity.getCreatedAt());
        }

        @Test
        @DisplayName("prePersist() NO debe sobreescribir un id ya existente")
        void prePersist_idYaExiste_noSobreescribe()
        {
            PetEntity entity = new PetEntity();
            entity.setPicture("photo.jpg");
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
            entity.setPicture("photo.jpg");
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
    }

    @Nested
    @DisplayName("User (entidad)")
    class UserEntityTest
    {
        @Test
        @DisplayName("builder debe construir la entidad con todos los campos incluyendo locked")
        void builder_construyeEntidad()
        {
            UUID id = UUID.randomUUID();
            // Field is named "locked" in the entity → builder uses .locked()
            User user = User.builder()
                    .id(id)
                    .username("alice")
                    .password("hashed")
                    .role(Role.ADMIN)
                    .locked(true)
                    .build();

            assertEquals(id, user.getId());
            assertEquals("alice", user.getUsername());
            assertEquals("hashed", user.getPassword());
            assertEquals(Role.ADMIN, user.getRole());
            assertTrue(user.isLocked());
        }

        @Test
        @DisplayName("builder con locked=false debe representar cuenta activa")
        void builder_lockedFalse_cuentaActiva()
        {
            User user = User.builder()
                    .username("bob")
                    .password("hashed")
                    .role(Role.NORMAL_USER)
                    .locked(false)
                    .build();

            assertFalse(user.isLocked());
        }

        @Test
        @DisplayName("no-args constructor should create an empty object")
        void constructorVacio_objetoVacio()
        {
            User user = new User();
            assertNull(user.getId());
            assertNull(user.getUsername());
            assertFalse(user.isLocked());
        }
    }
}