# Módulo: Infrastructure

## Propósito
Adaptador de salida (output adapter). Implementa los puertos definidos en `domain` usando Spring Data JPA. También gestiona la configuración de base de datos y los mappers de entidades.

## Estructura

```
infrastructure/
└── src/main/java/com/pumapunku/pet/infrastructure/
    ├── configuration/
    │   └── DatabaseProperties.java         # Propiedades de BD
    ├── mapper/
    │   ├── PetMapperInfrastructure.java     # MapStruct: PetEntity ↔ Pet
    │   └── UserMapper.java                  # MapStruct: User ↔ UserDomain
    └── repository/
        ├── PetRepository.java               # Adaptador (implementa dominio PetRepository)
        ├── PetRepositoryJPA.java            # Spring Data JPA (package-private)
        ├── RefugeRepositoryJPA.java         # Spring Data JPA para Refuge
        ├── UserRepository.java              # Implementación para usuarios
        ├── UserRepositoryImpl.java
        ├── UserCustomRepository.java
        ├── FilterCriteriaConverter.java     # Convierte Filter del dominio a Criteria JPA
        └── entity/
            ├── PetEntity.java
            ├── Refuge.java
            └── User.java
```

## Paginación en PetRepository

La implementación convierte el `PageRequest` del dominio (1-based) al `PageRequest` de Spring Data (0-based) mediante `pageRequest.zeroBasedPage()`:

```java
@Override
public Page<Pet> getPets(PageRequest pageRequest) {
    org.springframework.data.domain.PageRequest springPageRequest =
        org.springframework.data.domain.PageRequest.of(
            pageRequest.zeroBasedPage(), // page - 1
            pageRequest.size()
        );

    org.springframework.data.domain.Page<PetEntity> springPage =
        petRepositoryJPA.findAll(springPageRequest);

    List<Pet> pets = springPage.getContent().stream()
        .map(PetMapperInfrastructure.INSTANCE::toPet)
        .toList();

    return new Page<>(pets, pageRequest.page(), pageRequest.size(), springPage.getTotalElements());
}
```

> **Importante**: `pageRequest.page()` se devuelve tal cual (1-based) en el `Page` de dominio.
> La conversión 0-based es solo interna a Spring Data JPA.

## PetRepositoryJPA
Visibilidad `package-private` para encapsular la implementación JPA. Extiende `JpaRepository<PetEntity, UUID>`, lo que provee automáticamente `findAll(Pageable)` para paginación.

## Mappers (MapStruct)

### PetMapperInfrastructure
```
Pet → PetEntity : refugeId → refuge.id
PetEntity → Pet : refuge.id → refugeId
```

## Migraciones
Ubicadas en `src/main/resources/db/changelog/` gestionadas con Liquibase:
- `db.changelog-master.xml` — archivo maestro
- `1.0.0_database.xml` — DDL inicial

## Dependencias del módulo
```
infrastructure → domain
```
No depende de `application` ni de `presentation`.
