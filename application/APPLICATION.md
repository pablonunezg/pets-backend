# Módulo: Application

## Propósito
Orquesta los casos de uso del negocio. Depende únicamente del módulo `domain`. No conoce detalles de HTTP, JPA ni ningún framework externo.

## Estructura

```
application/
└── src/main/java/com/pumapunku/pet/application/
    ├── CreatePetInteractor.java      # Interfaz: crear mascota
    ├── DeletePetInteractor.java      # Interfaz: eliminar mascota
    ├── GetPetsInteractor.java        # Interfaz: obtener mascotas paginadas
    ├── UpdatePetInteractor.java      # Interfaz: actualizar mascota
    ├── impl/
    │   ├── CreatePetInteractorImpl.java
    │   ├── DeletePetInteractorImpl.java
    │   ├── GetPetsInteractorImpl.java
    │   └── UpdatePetInteractorImpl.java
    └── port/
        ├── FileStoragePort.java      # Puerto: almacenamiento de archivos
        └── UploadFile.java           # DTO interno de archivo a subir
```

## Casos de uso

### GetPetsInteractor
Recupera mascotas de forma paginada. Delega en `PetRepository.getPets(PageRequest)`.

```java
// Firma
Page<Pet> execute(PageRequest pageRequest);

// Uso con defaults
Page<Pet> page = getPetsInteractor.execute(PageRequest.ofDefaults());

// Uso con params explícitos
Page<Pet> page = getPetsInteractor.execute(new PageRequest(2, 10));
```

### CreatePetInteractor
Persiste la mascota y sube sus imágenes al storage.

```java
Pet execute(Pet pet, List<UploadFile> files);
```
Flujo: `petRepository.create(pet)` → `fileStoragePort.uploadFiles(files, pet.getId())` → asigna URL de imagen.

### UpdatePetInteractor
```java
void execute(Pet pet);
```
Delega directamente en `petRepository.update(pet)`.

### DeletePetInteractor
```java
void execute(UUID id);
```
Delega directamente en `petRepository.delete(id)`.

## Registro de beans
Los interactores usan `@Named` (Jakarta CDI) y `@RequiredArgsConstructor` (Lombok) para ser detectados por Spring como beans.

## Dependencias del módulo
```
application → domain
```
No depende de `infrastructure` ni de `presentation`.
