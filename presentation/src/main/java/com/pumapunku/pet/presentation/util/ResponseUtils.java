package com.pumapunku.pet.presentation.util;

import com.pumapunku.pet.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Utilidades genéricas para construir respuestas HTTP de la capa de presentación.
 *
 * <p>Centraliza el patrón de respuestas paginadas, evitando duplicar la lógica
 * de headers en cada controlador. El total de elementos se comunica al cliente
 * mediante el header estándar {@code X-Total-Count}.</p>
 *
 * <p>Uso en un controlador:</p>
 * <pre>{@code
 * Page<Pet> page = getPetsInteractor.execute(pageRequest);
 * List<PetResponse> content = page.content().stream()
 *         .map(PetMapper.INSTANCE::toPetResponse)
 *         .toList();
 * return ResponseUtils.paginatedOk(content, page);
 * }</pre>
 */
public final class ResponseUtils
{
    /** Header HTTP que expone el total de elementos disponibles sin paginar. */
    public static final String HEADER_TOTAL_COUNT = "X-Total-Count";

    private ResponseUtils()
    {
        // Clase utilitaria: no instanciable
    }

    /**
     * Construye un {@code 200 OK} con la lista de elementos y el header
     * {@value #HEADER_TOTAL_COUNT} obtenido del {@link Page} de dominio.
     *
     * @param <T>     tipo de los elementos del body.
     * @param content lista de elementos a devolver en el body.
     * @param page    página de dominio de la que se extrae {@code totalElements}.
     * @return {@link ResponseEntity} con status 200, body {@code content} y
     *         header {@code X-Total-Count: <totalElements>}.
     */
    public static <T> ResponseEntity<List<T>> paginatedOk(List<T> content, Page<?> page)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_TOTAL_COUNT, String.valueOf(page.totalElements()));
        return ResponseEntity.ok().headers(headers).body(content);
    }
}
