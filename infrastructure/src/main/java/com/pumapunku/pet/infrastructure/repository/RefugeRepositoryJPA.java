package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.infrastructure.repository.entity.Refuge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la entidad {@link Refuge}.
 *
 * <p>Expone las operaciones CRUD estándar sobre refugios, usado internamente
 * por {@link PetRepository} para validar la existencia del refugio al
 * crear una mascota. De visibilidad de paquete para limitar el acceso
 * directo desde fuera de la infraestructura.</p>
 */
interface RefugeRepositoryJPA extends JpaRepository<Refuge, UUID>
{
}
