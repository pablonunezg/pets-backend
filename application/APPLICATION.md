# application module

Contains use cases (interactors) and output ports.
Depends only on `domain`; has no framework dependencies.

## Interactors

| Interface              | Implementation             | Description                                                  |
|------------------------|----------------------------|--------------------------------------------------------------|
| `CreatePetInteractor`  | `CreatePetInteractorImpl`  | Persists a pet and uploads images to the bucket              |
| `GetPetsInteractor`    | `GetPetsInteractorImpl`    | Returns a paginated list of pets                             |
| `GetPetByIdInteractor` | `GetPetByIdInteractorImpl` | Finds a pet by UUID; throws `NotFoundException` if not found |
| `UpdatePetInteractor`  | `UpdatePetInteractorImpl`  | Updates pet data (with or without new image files)           |
| `DeletePetInteractor`  | `DeletePetInteractorImpl`  | Deletes the pet record from the database                     |

## Update scenarios (`UpdatePetInteractorImpl`)

- **Scenario 1 - with files:** deletes the old bucket files, uploads the new ones,
  then updates the pet record with the new picture URLs.
- **Scenario 2 - without files:** keeps the existing `picture` value from the
  database; only updates the remaining fields.

## Output ports

### FileStoragePort

```java
List<String> uploadFiles(List<UploadFile> files, UUID petId);
void         deleteFiles(String pictureUrls);   // comma-separated URLs
String       getBaseUrl();
```

`deleteFiles` receives the value of the `pet.picture` field (comma-separated public URLs)
and delegates the actual deletion to the infrastructure implementation (`SupabaseStorageService`).

### UploadFile (record)

Framework-agnostic wrapper for a file to be uploaded:

```java
record UploadFile(InputStream content, long size, String originalFilename, String contentType)
```

Passed to `FileStoragePort.uploadFiles()` so the application layer never depends on
Spring's `MultipartFile`.
