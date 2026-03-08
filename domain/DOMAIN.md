# domain module

Contains domain entities, business enums, exceptions, and repository ports.
Has no external dependencies (no JPA, no Spring, no frameworks).

## Main entities

### Pet

Represents a pet. Relevant fields:

- `status: Status` — current state (`FOR_ADOPTION`, `ADOPTED`, `MISSING`, `FOUND`)
- `userId: UUID` — FK to the user who registered the pet
- No `refugeId` — the `refuge_id` column has been removed from the `pet` table
- `picture: String` — comma-separated public URLs of images stored in Supabase Storage

### UserDomain

- `locked: boolean` — mirrors the `is_locked` column of the `app_user` table

## Enums

| Enum          | Values                                        |
|---------------|-----------------------------------------------|
| `Role`        | `SUPER_ADMIN`, `ADMIN`, `NORMAL_USER`         |
| `Status`      | `FOR_ADOPTION`, `ADOPTED`, `MISSING`, `FOUND` |
| `AgeGroup`    | `PUPPY`, `YOUNG`, `ADULT`, `SENIOR`           |
| `PetSize`     | `SMALL`, `MEDIUM`, `LARGE`, `EXTRA_LARGE`     |
| `Gender`      | `MALE`, `FEMALE`                              |
| `EnergyLevel` | `LOW`, `MODERATE`, `HIGH`                     |

## Pagination

- `PageRequest` — 1-based input; `zeroBasedPage()` converts to 0-based for Spring Data.
- `Page<T>` — generic paginated container; holds `content`, `totalElements`, `totalPages`, `isLast`.

## Filter DSL

A sealed `Filter` hierarchy with four implementations:

| Class             | Role                                          |
|-------------------|-----------------------------------------------|
| `ConditionFilter` | Leaf node — field / operator / value          |
| `CompositeFilter` | AND / OR of multiple filters                  |
| `NotFilter`       | Negation of a single filter                   |
| `FilterBuilder`   | Fluent builder; throws if no conditions added |

Supported operators in `RelationalOperator`: `EQ`, `NEQ`, `GT`, `GTE`, `LT`, `LTE`,
`LIKE`, `NOT_LIKE`, `IN`, `NOT_IN`, `IS_NULL`, `IS_NOT_NULL`, `BETWEEN`, `NOT_BETWEEN`.

## PetRepository port

```java
Pet              create(Pet pet);
void             update(Pet pet);
void             delete(UUID id);
Page<Pet>        getPets(PageRequest pageRequest);
Optional<Pet>    findById(UUID id);   // needed for the delete-with-files flow
```

## Exceptions

| Exception                | HTTP status (presentation) | When thrown                     |
|--------------------------|----------------------------|---------------------------------|
| `NotFoundException`      | 404                        | Entity not found by ID          |
| `AlreadyExistsException` | 409                        | Unique constraint violation     |
| `BusinessException`      | 422                        | Generic business rule violation |
