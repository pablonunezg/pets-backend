package com.pumapunku.pet.presentation.controller;

import com.pumapunku.pet.application.CreatePetInteractor;
import com.pumapunku.pet.application.DeletePetInteractor;
import com.pumapunku.pet.application.GetPetsInteractor;
import com.pumapunku.pet.application.UpdatePetInteractor;
import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.presentation.mapper.PetMapper;
import com.pumapunku.pet.presentation.request.PetRequest;
import com.pumapunku.pet.presentation.response.PetResponse;
import com.pumapunku.pet.presentation.security.User;
import com.pumapunku.pet.presentation.service.SupabaseStorageService;
import com.pumapunku.pet.presentation.util.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST que expone las operaciones CRUD sobre las mascotas.
 *
 * <p>Actúa como punto de entrada HTTP en la capa de presentación y delega
 * la lógica de negocio en los interactores de la capa de aplicación.</p>
 *
 * <p>Rutas disponibles bajo el prefijo {@code /pet}:</p>
 * <ul>
 *   <li>{@code GET  /pet}           — listado paginado; body = array de mascotas,
 *       header {@code X-Total-Count} = total de registros.</li>
 *   <li>{@code POST /pet}           — crear una nueva mascota (multipart).</li>
 *   <li>{@code PUT  /pet/{petId}}   — actualizar una mascota existente.</li>
 *   <li>{@code DELETE /pet/{petId}} — eliminar una mascota por ID.</li>
 * </ul>
 */
@RestController
@RequestMapping(value = "/pet")
@RequiredArgsConstructor
public class PetController
{
    /** Caso de uso: obtener el listado de mascotas. */
    private final transient GetPetsInteractor getPetsInteractor;

    /** Caso de uso: crear una nueva mascota (persiste + sube imágenes). */
    private final transient CreatePetInteractor createPetInteractor;

    /** Caso de uso: actualizar los datos de una mascota. */
    private final transient UpdatePetInteractor updatePetInteractor;

    /** Caso de uso: eliminar una mascota por su identificador. */
    private final transient DeletePetInteractor deletePetInteractor;

    /**
     * Retorna un array de mascotas de la página solicitada.
     *
     * <p>El total de registros disponibles (sin paginar) se comunica en el header
     * {@code X-Total-Count} para que el frontend pueda renderizar controles de
     * paginación sin necesidad de un objeto wrapper en el body.</p>
     *
     * <p>Si no se envían parámetros, se usan los valores por defecto:
     * {@code pageNumber=1} y {@code pageSize=200}.</p>
     *
     * <p>Ejemplo de respuesta:</p>
     * <pre>
     * HTTP/1.1 200 OK
     * X-Total-Count: 42
     *
     * [{"id": "...", "name": "Rex"}, ...]
     * </pre>
     *
     * @param pageNumber número de página solicitado (basado en 1); por defecto 1.
     * @param pageSize   cantidad de elementos por página; por defecto 200.
     * @return {@link ResponseEntity} con status {@code 200}, body {@code List<PetResponse>}
     *         y header {@code X-Total-Count}.
     */
    @GetMapping
    public ResponseEntity<List<PetResponse>> getPets(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "200") int pageSize)
    {
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        Page<Pet> page = getPetsInteractor.execute(pageRequest);

        List<PetResponse> content = page.content().stream()
                .map(PetMapper.INSTANCE::toPetResponse)
                .toList();

        return ResponseUtils.paginatedOk(content, page);
    }

    /**
     * Crea una nueva mascota junto con sus imágenes asociadas.
     *
     * <p>El {@code userId} se extrae automáticamente del token JWT del usuario
     * autenticado, por lo que no es necesario enviarlo en el body.</p>
     *
     * @param petRequest datos de la mascota a crear.
     * @param files      lista de imágenes a subir.
     * @return la entidad {@link Pet} persistida con su ID y URLs de imagen.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Pet createPet(
            @RequestParam("pet") @Valid PetRequest petRequest,
            @RequestPart("files") List<MultipartFile> files)
    {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pet pet = PetMapper.INSTANCE.toPet(petRequest);
        pet.setUserId(currentUser.getUserId());

        return createPetInteractor.execute(
                pet,
                SupabaseStorageService.toUploadFiles(files)
        );
    }

    /**
     * Actualiza los datos de una mascota existente.
     *
     * @param petId      identificador UUID de la mascota a actualizar (path variable).
     * @param petRequest nuevos datos de la mascota.
     */
    @PutMapping("{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String petId, @RequestBody PetRequest petRequest)
    {
        petRequest.setId(petId);
        Pet pet = PetMapper.INSTANCE.toPet(petRequest);
        updatePetInteractor.execute(pet);
    }

    /**
     * Elimina la mascota identificada por el UUID recibido.
     *
     * @param petId identificador UUID de la mascota a eliminar (path variable).
     */
    @DeleteMapping("{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String petId)
    {
        deletePetInteractor.execute(UUID.fromString(petId));
    }
}
