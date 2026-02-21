# Módulo: Domain

## Propósito
Contiene el núcleo del negocio, completamente aislado de frameworks y librerías externas. No depende de ningún otro módulo del proyecto.

## Estructura

```
domain/
└── src/main/java/com/pumapunku/pet/domain/
    ├── Page.java              # Contenedor genérico de resultados paginados
    ├── PageRequest.java       # Parámetros de paginación (1-based)
    ├── Pet.java               # Entidad principal del dominio
    ├── Role.java              # Enum de roles de usuario
    ├── UserDomain.java        # Entidad de usuario del dominio
    ├── exception/
    │   ├── AlreadyExistsException.java
    │   ├── BusinessException.java
    │   └── NotFoundException.java
    ├── filters/               # Sistema de filtros composables (patrón Composite)
    │   ├── Filter.java
    │   ├── CompositeFilter.java
    │   ├── ConditionFilter.java
    │   ├── FilterBuilder.java
    │   ├── LogicalOperator.java
    │   ├── NotFilter.java
    │   └── RelationalOperator.java
    └── repository/
        ├── PetRepository.java        # Puerto de salida para mascotas
        └── UserDomainRepository.java # Puerto de salida para usuarios
```

## Paginación

### PageRequest
- Numeración **1-based** (la primera página es la 1).
- `offset()` = `(page - 1) * size`
- `zeroBasedPage()` = `page - 1` → para uso en Spring Data / JPA
- `ofDefaults()` → página 1, tamaño 200

```java
PageRequest req = new PageRequest(1, 10);
req.offset();        // 0
req.zeroBasedPage(); // 0

PageRequest req2 = new PageRequest(3, 10);
req2.offset();        // 20
req2.zeroBasedPage(); // 2

PageRequest defaults = PageRequest.ofDefaults(); // page=1, size=200
```

### Page<T>
- Numera páginas igual que `PageRequest` (1-based).
- `isLast()` → `pageNumber >= totalPages()`
- `totalPages()` → ceil(totalElements / pageSize), mínimo 1

```java
Page<Pet> page = new Page<>(pets, 3, 10, 30L);
page.totalPages(); // 3
page.isLast();     // true (3 >= 3)
```

## Repositorios (puertos de salida)

### PetRepository
```java
Pet create(Pet pet);
void update(Pet pet);
void delete(UUID id);
Page<Pet> getPets(PageRequest pageRequest);
```

## Excepciones

| Excepción | Uso |
|-----------|-----|
| `NotFoundException` | Recurso no encontrado por ID |
| `AlreadyExistsException` | Duplicado al crear |
| `BusinessException` | Base genérica de excepciones de negocio |

## Reglas
- **Sin** dependencias de Spring, JPA, Jackson u otras librerías externas.
- Los repositorios son interfaces (puertos): la implementación vive en `infrastructure`.
- `Page` y `PageRequest` son `record` inmutables.
