# infrastructure module

Contains JPA adapters, MapStruct mappers, Liquibase changelog, and database configuration.
Depends on `application` (and transitively on `domain`).

## JPA Entities

### PetEntity (`pet` table)

- No `@ManyToOne` to `Refuge` — the `refuge_id` column was removed from the DDL.
- Field `status (Status)` — current adoption status.
- `@ManyToOne` to `User` via `user_id` is still present.
- `picture (VARCHAR 500)` — stores comma-separated public URLs of images in Supabase Storage.

### User (`app_user` table)

- Field `locked (boolean)` mapped to the `is_locked` column.
- Note: the column is `is_locked` but the Java field must be named `locked` (not `isLocked`)
  so that Lombok generates `getLocked()` / `setLocked()` and MapStruct can map it correctly.

### Refuge (`refuge` table)

- Remains as an independent entity.
- `addPet` / `removePet` helpers were removed since `refuge_id` no longer exists in `pet`.

## Mappers (MapStruct)

### PetMapperInfrastructure

- Maps `Pet ↔ PetEntity`.
- `userId ↔ user.id` mapping is explicit (`@Mapping(source = "user.id", target = "userId")`).
- `refugeId` mapping was removed.

### UserMapper

- Maps `User (entity) ↔ UserDomain`.
- Explicit mapping: `isLocked (entity field) → locked (UserDomain field)`.

## PetRepository (infrastructure adapter)

Implements `PetRepository` from the domain:

| Method       | Notes                                                                  |
|--------------|------------------------------------------------------------------------|
| `create()`   | Looks up the `User` by `userId`, sets the relation, then saves.        |
| `update()`   | Retrieves the existing entity and merges updated fields.               |
| `delete()`   | Deletes by primary key.                                                |
| `getPets()`  | Uses `FilterCriteriaConverter` + JPA Criteria API for dynamic queries. |
| `findById()` | Returns `Optional<Pet>`; used by the delete-with-files flow.           |

## FilterCriteriaConverter

Converts the domain `Filter` DSL into JPA `Predicate` objects:

- `ConditionFilter` → simple predicate (EQ, LIKE, BETWEEN, IN, IS_NULL, …)
- `CompositeFilter` → `cb.and(…)` / `cb.or(…)`
- `NotFilter`       → `cb.not(…)`
- Nested fields (e.g. `user.email`) are resolved via `Root.get()` chaining.

## Liquibase changelog (`1.0.0_database.xml`)

| Changeset | Description                                                         |
|-----------|---------------------------------------------------------------------|
| 1         | Create table `refuge`                                               |
| 2         | Create table `app_user` (with `role`)                               |
| 3         | Create table `pet` (no `refuge_id` FK)                              |
| 4         | Add column `is_locked BOOLEAN NOT NULL DEFAULT false` to `app_user` |
| 5         | Add column `status` to `pet`                                        |
| 6         | Add column `picture VARCHAR(500)` to `pet`                          |
| 7         | (Corrective) Ensure `pet` table has no `refuge_id` column           |
