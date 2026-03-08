package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.domain.Page;
import com.pumapunku.pet.domain.PageRequest;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.exception.NotFoundException;
import com.pumapunku.pet.infrastructure.mapper.PetMapperInfrastructure;
import com.pumapunku.pet.infrastructure.repository.entity.PetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the domain repository {@link com.pumapunku.pet.domain.repository.PetRepository}
 * using Spring Data JPA.
 *
 * <p>Acts as an output adapter in the hexagonal architecture,
 * translating domain calls into JPA operations on {@link PetEntity},
 * and converting results back to the domain model {@link Pet} via
 * {@link PetMapperInfrastructure}.</p>
 */
@Repository
@RequiredArgsConstructor
public class PetRepository implements com.pumapunku.pet.domain.repository.PetRepository
{
    /**
     * JPA repository for CRUD operations on {@link PetEntity}.
     */
    private final transient PetRepositoryJPA petRepositoryJPA;

    /**
     * {@inheritDoc}
     *
     * <p>Persists the entity directly without validating a shelter,
     * since the {@code refuge_id} column was removed from the {@code pet} table.</p>
     */
    @Override
    public Pet create(Pet pet)
    {
        PetEntity entity = PetMapperInfrastructure.INSTANCE.toPetEntity(pet);
        PetEntity saved = petRepositoryJPA.save(entity);
        return PetMapperInfrastructure.INSTANCE.toPet(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Loads the existing entity to preserve the managed {@code User} reference
     * and avoid a transient entity error on {@code save()}. Only editable fields
     * are updated; {@code user} and {@code createdAt} are kept intact.</p>
     */
    @Override
    public void update(Pet pet)
    {
        PetEntity existing = petRepositoryJPA.findById(pet.getId())
                .orElseThrow(() -> new NotFoundException("Pet", pet.getId().toString()));

        PetMapperInfrastructure.INSTANCE.updatePetEntity(pet, existing);

        petRepositoryJPA.save(existing);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Verifies that the pet exists before deleting.
     * Throws {@link NotFoundException} if it does not exist.</p>
     */
    @Override
    public void delete(UUID id)
    {
        if (!petRepositoryJPA.existsById(id))
        {
            throw new NotFoundException("Pet", id.toString());
        }

        petRepositoryJPA.deleteById(id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Performs a paginated query using Spring Data JPA.
     * Converts the 1-based domain {@link PageRequest} to the 0-based
     * Spring {@link org.springframework.data.domain.PageRequest}
     * via {@link PageRequest#zeroBasedPage()}.</p>
     */
    @Override
    public Page<Pet> getPets(PageRequest pageRequest)
    {
        org.springframework.data.domain.PageRequest springPageRequest =
                org.springframework.data.domain.PageRequest.of(
                        pageRequest.zeroBasedPage(),
                        pageRequest.size()
                );

        org.springframework.data.domain.Page<PetEntity> springPage =
                petRepositoryJPA.findAll(springPageRequest);

        List<Pet> pets = springPage.getContent()
                .stream()
                .map(PetMapperInfrastructure.INSTANCE::toPet)
                .toList();

        return new Page<>(pets, pageRequest.page(), pageRequest.size(), springPage.getTotalElements());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Pet> findById(UUID id)
    {
        return petRepositoryJPA.findById(id)
                .map(PetMapperInfrastructure.INSTANCE::toPet);
    }
}
