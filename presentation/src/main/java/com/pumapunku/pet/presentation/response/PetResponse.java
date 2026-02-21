package com.pumapunku.pet.presentation.response;

import com.pumapunku.pet.domain.AgeGroup;
import com.pumapunku.pet.domain.EnergyLevel;
import com.pumapunku.pet.domain.Gender;
import com.pumapunku.pet.domain.Size;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta que representa los datos públicos de una mascota.
 *
 * <p>Es devuelto por los endpoints de consulta ({@code GET /pet}) y
 * expone todos los campos del dominio necesarios para el cliente, evitando
 * que la representación interna quede expuesta directamente.</p>
 *
 * @param id            identificador único de la mascota.
 * @param name          nombre de la mascota.
 * @param picture       URLs de las imágenes, separadas por comas.
 * @param breed         raza del animal.
 * @param ageGroup      grupo de edad.
 * @param size          tamaño corporal.
 * @param gender        género.
 * @param isNeutered    indica si fue esterilizado.
 * @param goodWithDogs  indica si convive bien con perros.
 * @param goodWithCats  indica si convive bien con gatos.
 * @param goodWithKids  indica si convive bien con niños.
 * @param energyLevel   nivel de energía.
 * @param refugeId      identificador del refugio propietario.
 * @param userId        identificador del usuario que registró la mascota.
 * @param createdAt     fecha y hora de registro.
 */
public record PetResponse(
        UUID id,
        String name,
        String picture,
        String breed,
        AgeGroup ageGroup,
        Size size,
        Gender gender,
        boolean isNeutered,
        boolean goodWithDogs,
        boolean goodWithCats,
        boolean goodWithKids,
        EnergyLevel energyLevel,
        UUID refugeId,
        UUID userId,
        LocalDateTime createdAt
)
{
}
