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
import java.util.UUID;

/**
 * Implementación del repositorio de dominio {@link com.pumapunku.pet.domain.repository.PetRepository}
 * usando Spring Data JPA.
 *
 * <p>Actúa como adaptador de salida (output adapter) en la arquitectura hexagonal,
 * traduciendo las llamadas del dominio a operaciones JPA sobre {@link PetEntity},
 * y convirtiendo los resultados de vuelta al modelo de dominio {@link Pet} mediante
 * {@link PetMapperInfrastructure}.</p>
 *
 * <p>Valida la existencia del refugio antes de crear una mascota y verifica
 * que la mascota exista antes de actualizar o eliminar.</p>
 */
@Repository
@RequiredArgsConstructor
public class PetRepository implements com.pumapunku.pet.domain.repository.PetRepository
{
    /** Repositorio JPA para las operaciones CRUD sobre {@link PetEntity}. */
    private final transient PetRepositoryJPA petRepositoryJPA;

    /** Repositorio JPA para verificar la existencia del refugio. */
    private final transient RefugeRepositoryJPA refugeRepositoryJPA;

    /**
     * {@inheritDoc}
     *
     * <p>Verifica que el refugio indicado en la mascota exista antes de persistirla.
     * Si el refugio no existe, lanza {@link NotFoundException}.</p>
     */
    @Override
    public Pet create(Pet pet)
    {
        PetEntity petEntity = refugeRepositoryJPA.findById(pet.getRefugeId())
                .map(refuge -> petRepositoryJPA.save(PetMapperInfrastructure.INSTANCE.toPetEntity(pet)))
                .orElseThrow(() -> new NotFoundException(pet.getRefugeId().toString(), "Refuge not found"));

        return PetMapperInfrastructure.INSTANCE.toPet(petEntity);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Verifica que la mascota exista antes de actualizar.
     * Si no existe, lanza {@link NotFoundException}.</p>
     */
    @Override
    public void update(Pet pet)
    {
        if (!petRepositoryJPA.existsById(pet.getId()))
        {
            throw new NotFoundException("Pet", pet.getId().toString());
        }

        petRepositoryJPA.save(PetMapperInfrastructure.INSTANCE.toPetEntity(pet));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Verifica que la mascota exista antes de eliminar.
     * Si no existe, lanza {@link NotFoundException}.</p>
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
     * <p>Realiza una consulta paginada usando Spring Data JPA.
     * Convierte el {@link PageRequest} de dominio (1-based) al
     * {@link org.springframework.data.domain.PageRequest} de Spring (0-based)
     * mediante {@link PageRequest#zeroBasedPage()}.</p>
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
}
