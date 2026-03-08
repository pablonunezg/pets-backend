# presentation module

Contains REST controllers, JWT security configuration, and the Supabase storage service.
Depends on `application` (and transitively on `domain`).

## PetController (`/pet`)

| Method | Path           | Description                                        |
|--------|----------------|----------------------------------------------------|
| GET    | `/pet`         | Paginated list; exposes `X-Total-Count` header     |
| POST   | `/pet`         | Create pet + upload images (multipart form)        |
| PUT    | `/pet/{petId}` | Update pet (with or without new image files)       |
| DELETE | `/pet/{petId}` | **Delete bucket files then remove the pet record** |

### DELETE flow

1. `getPetByIdInteractor.execute(uuid)` → retrieves the pet and its image URLs.
2. `supabaseStorageService.deleteFiles(pet.getPicture())` → deletes files from the bucket.
3. `deletePetInteractor.execute(uuid)` → deletes the database record.

### Pagination query parameters

| Param  | Default | Description              |
|--------|---------|--------------------------|
| `page` | `1`     | Page number (1-based)    |
| `size` | `10`    | Number of items per page |

The response body is a `PaginatedResponse<PetResponse>`; total element count is also
returned in the `X-Total-Count` header via `ResponseUtils.paginatedOk()`.

## SupabaseStorageService

Implements `FileStoragePort`. Communicates with the Supabase REST API.

| Method            | Description                                                          |
|-------------------|----------------------------------------------------------------------|
| `uploadFiles()`   | Uploads each file on a separate virtual thread; returns public URLs. |
| `deleteFiles()`   | Parses comma-separated URLs, strips the public prefix, sends DELETE. |
| `getBaseUrl()`    | Returns `<url>public/<bucket>/`.                                     |
| `toUploadFiles()` | Static helper — converts `MultipartFile` list to `UploadFile` list.  |

Required properties:

```properties
supabase.url=https://<project>.supabase.co/storage/v1/object/
supabase.apikey=<service-role-key>
supabase.bucket=<bucket-name>
```

## JWT Security

### JwtUtil

| Method              | Description                                  |
|---------------------|----------------------------------------------|
| `generateToken()`   | Signs a JWT with username claim; uses HS256. |
| `extractUsername()` | Reads the `sub` claim.                       |
| `isTokenValid()`    | Checks username match and expiration.        |
| `isTokenExpired()`  | Compares expiration date to now.             |

Required properties:

```properties
jwt.secret=<base64-encoded-256-bit-secret>
jwt.expiration=<milliseconds>   # e.g. 86400000 for 24 h
```

### JwtAuthenticationFilter

Extends `OncePerRequestFilter`. On every request:

1. Reads the `Authorization: Bearer <token>` header.
2. Extracts and validates the JWT via `JwtUtil`.
3. Sets `UsernamePasswordAuthenticationToken` in `SecurityContextHolder`.
4. Returns `401` JSON error for expired / malformed tokens.

### User (UserDetails)

The `locked` boolean field (sourced from `app_user.is_locked`) controls account locking:

```java
public boolean isAccountNonLocked() { return !locked; }
```

`UserDetailsServiceImpl` loads `locked` from `UserDomain` when building the security `User`.

## DTOs

### PetRequest

- No `refugeId` field.
- `status (Status)` — required (`@NotNull`).
- `picture` — no `@NotBlank`; always set by the server (upload or preserved from DB).
- `id` — assigned internally on PUT; the client does not send it.

### PetResponse

- No `refugeId` field.
- `status (Status)` included.
- `isNeutered` mapped explicitly from `Pet.neutered` (MapStruct limitation with record accessor names).

## Error handling (`PetApiExceptionHandler`)

| Exception                | HTTP status | Response message                    |
|--------------------------|-------------|-------------------------------------|
| `NotFoundException`      | 404         | `"Resource not found"`              |
| `AlreadyExistsException` | 409         | `"Resource already exists"`         |
| `BusinessException`      | 422         | `"Business rule violation"`         |
| `MethodArgumentNotValid` | 400         | `"Data validation failed"` + errors |
| Unexpected `Exception`   | 500         | `"Internal server error"`           |
