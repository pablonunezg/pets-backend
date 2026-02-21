# Módulo: Presentation

## Propósito
Adaptador de entrada (input adapter). Expone los casos de uso como endpoints REST HTTP. Gestiona serialización/deserialización, seguridad JWT, CORS y manejo de errores.

## Estructura

```
presentation/
└── src/main/java/com/pumapunku/pet/presentation/
    ├── PetApiExceptionHandler.java
    ├── controller/
    │   └── PetController.java
    ├── converter/
    │   └── PetConverter.java
    ├── mapper/
    │   └── PetMapper.java
    ├── request/
    │   └── PetRequest.java
    ├── response/
    │   ├── PaginatedResponse.java     # DTO genérico (disponible para uso futuro)
    │   └── PetResponse.java
    ├── security/
    │   ├── AuthController.java
    │   ├── AuthRequest.java
    │   ├── CorsProperties.java        # @ConfigurationProperties(prefix="cors")
    │   ├── JwtAuthenticationFilter.java
    │   ├── JwtUtil.java
    │   ├── PasswordEncoderConfig.java
    │   ├── SecurityConfig.java        # CORS + JWT filter chain
    │   ├── User.java
    │   └── UserDetailsServiceImpl.java
    ├── service/
    │   └── SupabaseStorageService.java
    └── util/
        └── ResponseUtils.java         # Helper genérico: array + X-Total-Count
```

## Endpoint: GET /pet (paginado)

### Request
```
GET /pet?pageNumber=1&pageSize=200
```
| Param | Tipo | Default | Descripción |
|-------|------|---------|-------------|
| `pageNumber` | int | `1` | Página solicitada (1-based) |
| `pageSize` | int | `200` | Elementos por página |

### Response `200 OK`
```
X-Total-Count: 42

[
  { "id": "uuid", "name": "Rex" },
  { "id": "uuid", "name": "Luna" }
]
```

El body es directamente un **array JSON** — sin wrapper. El total de registros
disponibles (sin paginar) se obtiene del header `X-Total-Count`.

## ResponseUtils — helper genérico de paginación

Cualquier endpoint que devuelva una lista paginada debe usar este helper para
mantener consistencia en todos los controladores:

```java
// En cualquier controlador
Page<MiEntidad> page = miInteractor.execute(pageRequest);
List<MiResponse> content = page.content().stream()
        .map(MiMapper.INSTANCE::toResponse)
        .toList();
return ResponseUtils.paginatedOk(content, page);
```

El método devuelve `ResponseEntity<List<T>>` con:
- Status `200 OK`
- Body: la lista recibida
- Header `X-Total-Count`: valor de `page.totalElements()`

### Constante del header
```java
ResponseUtils.HEADER_TOTAL_COUNT // "X-Total-Count"
```

## Cómo leer X-Total-Count en React/TypeScript (Vite)

```typescript
const response = await fetch('/pet?pageNumber=1&pageSize=10', {
  credentials: 'include', // necesario para cookies de autenticación
});

const totalCount = Number(response.headers.get('X-Total-Count'));
const pets = await response.json();
```

Con `axios`:
```typescript
const { data, headers } = await axios.get('/pet', {
  params: { pageNumber: 1, pageSize: 10 },
  withCredentials: true,
});
const totalCount = Number(headers['x-total-count']);
```

## CORS

### Configuración en `application.properties`
```properties
cors.allowed-origin=http://midominio.local:5173
cors.allow-credentials=true
```

### CorsProperties
Clase `@ConfigurationProperties(prefix = "cors")` que inyecta las propiedades:
- `allowedOrigin` → origen del frontend
- `allowCredentials` → habilita cookies en CORS

### SecurityConfig
- Métodos permitidos: `GET, POST, PUT, DELETE, OPTIONS`
- Headers permitidos: `*` (todos)
- Headers expuestos al cliente: `X-Total-Count`
- `allowCredentials=true` → el navegador puede enviar/recibir cookies

> **Nota**: con `allowCredentials=true` no se puede usar `allowedOrigins=*`.
> Siempre especifica el origen exacto en `cors.allowed-origin`.

## Otros endpoints

| Método | Ruta | Status | Descripción |
|--------|------|--------|-------------|
| `POST` | `/pet` | `200` | Crear mascota (multipart/form-data) |
| `PUT` | `/pet/{petId}` | `204` | Actualizar mascota |
| `DELETE` | `/pet/{petId}` | `204` | Eliminar mascota |
| `POST` | `/auth/login` | `200` | Obtener JWT |

## Manejo de errores

| Excepción | HTTP Status |
|-----------|-------------|
| `NotFoundException` | `404 Not Found` |
| `AlreadyExistsException` | `409 Conflict` |
| `BusinessException` | `400 Bad Request` |

## Seguridad
JWT stateless. El filtro `JwtAuthenticationFilter` intercepta todas las rutas salvo `/auth/**`.
Token generado en `AuthController` tras validar credenciales con `UserDetailsServiceImpl`.

## Dependencias del módulo
```
presentation → application → domain
```
