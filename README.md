# pets-backend

REST backend for managing shelter pets, built with Spring Boot and hexagonal architecture.

## Architecture

The project is split into four Gradle modules:

| Module           | Responsibility                                            |
|------------------|-----------------------------------------------------------|
| `domain`         | Domain entities, repository ports, business enums         |
| `application`    | Use cases (interactors), output ports (`FileStoragePort`) |
| `infrastructure` | JPA adapters, mappers, Liquibase, database configuration  |
| `presentation`   | REST controllers, JWT security, `SupabaseStorageService`  |

## Module dependency graph

```
presentation   → application → domain
infrastructure → application → domain
```

## Domain — Enums and database values

| Enum          | DB column          | Values                                        |
|---------------|--------------------|-----------------------------------------------|
| `Role`        | `app_user.role`    | `SUPER_ADMIN`, `ADMIN`, `NORMAL_USER`         |
| `Status`      | `pet.status`       | `FOR_ADOPTION`, `ADOPTED`, `MISSING`, `FOUND` |
| `AgeGroup`    | `pet.age_group`    | `PUPPY`, `YOUNG`, `ADULT`, `SENIOR`           |
| `Size`        | `pet.petSize`      | `SMALL`, `MEDIUM`, `LARGE`, `EXTRA_LARGE`     |
| `Gender`      | `pet.gender`       | `MALE`, `FEMALE`                              |
| `EnergyLevel` | `pet.energy_level` | `LOW`, `MODERATE`, `HIGH`                     |

## Table `app_user`

Column `is_locked` (BOOLEAN): when `true` the account is locked.
Spring Security calls `isAccountNonLocked()` on `User` (presentation), which returns `!locked`.

## Table `pet`

The `refuge_id` column has been removed. Pets no longer have a direct FK to `refuge`.
Image files are stored in Supabase Storage under the configured bucket.

## Pet deletion flow

1. `PetController.delete()` retrieves the pet to read its image URLs.
2. Calls `SupabaseStorageService.deleteFiles(pictureUrls)` to remove the files from the bucket.
3. Calls `DeletePetInteractor.execute(id)` to delete the database record.

## Required configuration (`application.properties`)

```properties
supabase.url=https://<project>.supabase.co/storage/v1/object/
supabase.apikey=<service-role-key>
supabase.bucket=<bucket-name>

jwt.secret=<base64-encoded-secret>
jwt.expiration=<expiration-in-ms>
```
