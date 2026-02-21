package com.pumapunku.pet.domain;

import java.util.List;

/**
 * Contenedor genérico de resultados paginados del dominio.
 *
 * <p>Encapsula una página de resultados junto con metadatos de paginación,
 * sin dependencias de Spring Data ni de ninguna librería de infraestructura.</p>
 *
 * <p>Utiliza numeración de páginas <strong>basada en 1</strong>
 * (la primera página es la 1), consistente con {@link PageRequest}.</p>
 *
 * @param <T>           tipo de los elementos contenidos en la página.
 * @param content       lista de elementos de la página actual.
 * @param pageNumber    número de página actual (basado en 1).
 * @param pageSize      cantidad máxima de elementos por página.
 * @param totalElements total de elementos que satisfacen la consulta.
 */
public record Page<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements
)
{
    /**
     * Calcula el número total de páginas disponibles.
     *
     * @return total de páginas; al menos 1 aunque no haya elementos.
     */
    public int totalPages()
    {
        return pageSize == 0 ? 1 : (int) Math.ceil((double) totalElements / pageSize);
    }

    /**
     * Indica si esta es la última página disponible.
     *
     * <p>Dado que la numeración es 1-based, la última página se alcanza
     * cuando {@code pageNumber >= totalPages()}.</p>
     *
     * @return {@code true} si no hay más páginas después de esta.
     */
    public boolean isLast()
    {
        return pageNumber >= totalPages();
    }
}
