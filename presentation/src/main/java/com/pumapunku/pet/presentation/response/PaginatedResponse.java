package com.pumapunku.pet.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO genérico de respuesta paginada para los endpoints REST.
 *
 * <p>Envuelve una lista de resultados junto con los metadatos de paginación
 * necesarios para que el cliente pueda navegar entre páginas.</p>
 *
 * @param <T> tipo de los elementos contenidos en la respuesta.
 */
@Getter
@Setter
@AllArgsConstructor
public class PaginatedResponse<T>
{
    /** Lista de elementos de la página actual. */
    private List<T> content;

    /** Número de página actual (basado en 1). */
    private Integer page;

    /** Tamaño máximo de la página. */
    private Integer size;

    /** Indica si esta es la última página disponible. */
    private Boolean isLast;

    /** Número total de páginas disponibles. */
    private Integer totalPages;

    /** Total de elementos que satisfacen la consulta (sin paginar). */
    private Long totalElements;

    /**
     * Método de fábrica para construir una respuesta paginada de forma conveniente.
     *
     * @param <T>           tipo de los elementos.
     * @param content       lista de elementos de la página.
     * @param page          número de página actual (basado en 1).
     * @param size          tamaño de la página.
     * @param isLast        {@code true} si no hay más páginas.
     * @param totalPages    total de páginas disponibles.
     * @param totalElements total de elementos sin paginar.
     * @return nueva instancia de {@link PaginatedResponse}.
     */
    public static <T> PaginatedResponse<T> from(List<T> content, Integer page, Integer size,
                                                 Boolean isLast, Integer totalPages, Long totalElements)
    {
        return new PaginatedResponse<>(content, page, size, isLast, totalPages, totalElements);
    }
}
