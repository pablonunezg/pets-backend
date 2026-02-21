package com.pumapunku.pet.presentation.mapper;

import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.presentation.request.PetRequest;
import com.pumapunku.pet.presentation.response.PetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper MapStruct de la capa de presentación para convertir entre
 * los DTOs HTTP y el modelo de dominio {@link Pet}.
 *
 * <p>Convierte:</p>
 * <ul>
 *   <li>{@link PetRequest} → {@link Pet} — al recibir una solicitud de creación/actualización.</li>
 *   <li>{@link Pet} → {@link PetResponse} — al preparar la respuesta HTTP.</li>
 * </ul>
 *
 * <p>El campo {@code createdAt} es ignorado al mapear desde {@link PetRequest}
 * porque es generado automáticamente en la capa de persistencia.</p>
 */
@Mapper(componentModel = "spring")
public interface PetMapper
{
    /** Instancia singleton generada por MapStruct para uso sin inyección de dependencias. */
    PetMapper INSTANCE = Mappers.getMapper(PetMapper.class);

    /**
     * Convierte un {@link PetRequest} al modelo de dominio {@link Pet}.
     *
     * @param petRequest DTO recibido en la petición HTTP; no debe ser {@code null}.
     * @return modelo de dominio equivalente.
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Pet toPet(PetRequest petRequest);

    /**
     * Convierte el modelo de dominio {@link Pet} a un {@link PetResponse}.
     *
     * @param pet modelo de dominio; no debe ser {@code null}.
     * @return DTO de respuesta HTTP con los datos públicos de la mascota.
     */
    PetResponse toPetResponse(Pet pet);
}
