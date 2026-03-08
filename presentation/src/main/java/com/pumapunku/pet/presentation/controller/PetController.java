package com.pumapunku.pet.presentation.controller;

import com.pumapunku.pet.application.*;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * REST controller exposing CRUD operations for pets.
 *
 * <p>Acts as the HTTP entry point in the presentation layer and delegates
 * all business logic to the application layer interactors.</p>
 *
 * <p>Available routes under the {@code /pet} prefix:</p>
 * <ul>
 *   <li>{@code GET    /pet}           — paginated listing.</li>
 *   <li>{@code POST   /pet}           — create a new pet (multipart).</li>
 *   <li>{@code PUT    /pet/{petId}}   — update an existing pet (multipart).</li>
 *   <li>{@code DELETE /pet/{petId}}   — delete pet and its bucket files.</li>
 * </ul>
 */
@RestController
@RequestMapping(value = "/pet")
@RequiredArgsConstructor
@Validated
public class PetController
{
    /**
     * Use case: retrieve the pet list.
     */
    private final transient GetPetsInteractor getPetsInteractor;

    /**
     * Use case: retrieve a pet by ID.
     */
    private final transient GetPetByIdInteractor getPetByIdInteractor;

    /**
     * Use case: create a new pet (persists + uploads images).
     */
    private final transient CreatePetInteractor createPetInteractor;

    /**
     * Use case: update pet data.
     */
    private final transient UpdatePetInteractor updatePetInteractor;

    /**
     * Use case: delete a pet by its identifier.
     */
    private final transient DeletePetInteractor deletePetInteractor;

    /**
     * Storage service for uploading and deleting files from the bucket.
     */
    private final transient SupabaseStorageService supabaseStorageService;

    /**
     * Returns an array of pets for the requested page.
     *
     * @param pageNumber requested page number (1-based); default 1.
     * @param pageSize   number of elements per page; default 200.
     * @return {@link ResponseEntity} with status {@code 200}, body {@code List<PetResponse>}
     * and header {@code X-Total-Count}.
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
     * Creates a new pet together with its associated images.
     *
     * <p>The {@code userId} is automatically extracted from the authenticated user's
     * JWT token.</p>
     *
     * @param petRequest pet data to create.
     * @param files      list of images to upload to the bucket.
     * @return the persisted {@link Pet} entity with its ID and image URLs.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Pet createPet(
            @RequestParam("pet") @Valid PetRequest petRequest,
            @RequestPart("files") List<MultipartFile> files)
    {
        User currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                .getPrincipal();

        Pet pet = PetMapper.INSTANCE.toPet(petRequest);
        pet.setUserId(Objects.requireNonNull(currentUser).getUserId());

        return createPetInteractor.execute(
                pet,
                SupabaseStorageService.toUploadFiles(files)
        );
    }

    /**
     * Updates the data of an existing pet.
     *
     * <p>Fully delegates file logic to the interactor:</p>
     * <ul>
     *   <li><strong>Con {@code files}</strong>: el interactor elimina los archivos actuales
     *       from the bucket, uploads the new ones and updates {@code picture}.</li>
     *   <li><strong>Sin {@code files}</strong>: el interactor conserva la {@code picture}
     *       and only updates the database.</li>
     * </ul>
     *
     * @param petId      UUID identifier of the pet to update.
     * @param petRequest new pet data.
     * @param files      optional images; if empty the existing ones are kept.
     */
    @PutMapping(value = "{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable String petId,
            @RequestParam("pet") @Valid PetRequest petRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files)
    {
        petRequest.setId(petId);
        Pet pet = PetMapper.INSTANCE.toPet(petRequest);

        updatePetInteractor.execute(
                pet,
                files != null ? SupabaseStorageService.toUploadFiles(files) : List.of()
        );
    }

    /**
     * Deletes the pet identified by the received UUID and removes its bucket files.
     *
     * <p>The deletion flow is:</p>
     * <ol>
     *   <li>Fetch the pet to read its image URLs.</li>
     *   <li>Delete the files from the Supabase bucket.</li>
     *   <li>Delete the pet from the database.</li>
     * </ol>
     *
     * @param petId UUID identifier of the pet to delete.
     */
    @DeleteMapping("{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String petId)
    {
        UUID uuid = UUID.fromString(petId);
        Pet pet = getPetByIdInteractor.execute(uuid);
        supabaseStorageService.deleteFiles(pet.getPicture());
        deletePetInteractor.execute(uuid);
    }
}
