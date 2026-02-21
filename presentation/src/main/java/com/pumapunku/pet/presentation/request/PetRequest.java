package com.pumapunku.pet.presentation.request;

import com.pumapunku.pet.domain.AgeGroup;
import com.pumapunku.pet.domain.EnergyLevel;
import com.pumapunku.pet.domain.Gender;
import com.pumapunku.pet.domain.Size;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO que representa los datos de una mascota enviados en una petición HTTP.
 *
 * <p>Es usado tanto para la creación ({@code POST /pet}) como para la
 * actualización ({@code PUT /pet/{id}}) de mascotas. Los campos marcados con
 * {@link NotNull} son obligatorios y son validados automáticamente por
 * Bean Validation antes de llegar al controlador.</p>
 */
@NoArgsConstructor
@Getter
@Setter
public class PetRequest
{
    /** Identificador de la mascota; se asigna en actualizaciones ({@code PUT}). */
    private String id;

    /** Nombre de la mascota. Obligatorio. */
    @NotEmpty
    private String name;

    /** URL o nombre del archivo de imagen de la mascota. Obligatorio. */
    @NotEmpty
    private String picture;

    /** Raza de la mascota. Obligatorio. */
    @NotEmpty
    private String breed;

    /** Grupo de edad de la mascota. Obligatorio. */
    @NotNull
    private AgeGroup ageGroup;

    /** Tamaño del animal. Obligatorio. */
    @NotNull
    private Size size;

    /** Género de la mascota. Obligatorio. */
    @NotNull
    private Gender gender;

    /** Indica si la mascota ha sido esterilizada. */
    private boolean isNeutered;

    /** Indica si la mascota convive bien con perros. */
    private boolean goodWithDogs;

    /** Indica si la mascota convive bien con gatos. */
    private boolean goodWithCats;

    /** Indica si la mascota convive bien con niños. */
    private boolean goodWithKids;

    /** Nivel de energía del animal. Obligatorio. */
    @NotNull
    private EnergyLevel energyLevel;

    /** Identificador del refugio al que pertenece la mascota. Obligatorio. */
    @NotNull
    private UUID refugeId;
}
