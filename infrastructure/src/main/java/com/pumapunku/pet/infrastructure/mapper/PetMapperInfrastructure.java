package com.pumapunku.pet.infrastructure.mapper;

import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.infrastructure.repository.entity.PetEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper MapStruct para convertir entre la entidad JPA {@link PetEntity}
 * y el modelo de dominio {@link Pet}.
 *
 * <p>Gestiona el mapeo de la relación {@code ManyToOne} con {@code Refuge}:
 * al convertir de dominio a entidad, el campo plano {@code refugeId} se asigna
 * como {@code refuge.id}; en el sentido inverso, {@code refuge.id} se extrae
 * como {@code refugeId}.</p>
 *
 * <p>La instancia {@link #INSTANCE} puede usarse en contextos donde la
 * inyección de dependencias no está disponible (por ejemplo, en métodos
 * {@code static} o en clases que no son beans de Spring).</p>
 */
@Mapper(componentModel = "spring")
public interface PetMapperInfrastructure
{
    /** Instancia singleton generada por MapStruct para uso sin inyección. */
    PetMapperInfrastructure INSTANCE = Mappers.getMapper(PetMapperInfrastructure.class);

    /**
     * Convierte un modelo de dominio {@link Pet} en la entidad JPA {@link PetEntity}.
     *
     * @param pet modelo de dominio; no debe ser {@code null}.
     * @return entidad JPA lista para ser persistida.
     */
    @Mapping(source = "refugeId", target = "refuge.id")
    @Mapping(source = "userId", target = "user.id")
    PetEntity toPetEntity(Pet pet);

    /**
     * Convierte una entidad JPA {@link PetEntity} en el modelo de dominio {@link Pet}.
     *
     * @param petEntity entidad JPA recuperada de la base de datos; no debe ser {@code null}.
     * @return modelo de dominio listo para ser procesado por la capa de aplicación.
     */
    @Mapping(source = "refuge.id", target = "refugeId")
    @Mapping(source = "user.id", target = "userId")
    Pet toPet(PetEntity petEntity);
}
