package com.pumapunku.pet.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Generic paginated response DTO for REST endpoints.
 *
 * <p>Wraps a list of results together with the pagination metadata
 * needed for the client to navigate between pages.</p>
 *
 * @param <T> type of the elements contained in the response.
 */
@Getter
@Setter
@AllArgsConstructor
public class PaginatedResponse<T>
{
    /**
     * List of elements on the current page.
     */
    private List<T> content;

    /**
     * Current page number (1-based).
     */
    private Integer page;

    /**
     * Maximum page size.
     */
    private Integer size;

    /**
     * Indicates whether this is the last available page.
     */
    private Boolean isLast;

    /**
     * Total number of available pages.
     */
    private Integer totalPages;

    /**
     * Total number of elements matching the query (unpaged).
     */
    private Long totalElements;

    /**
     * Factory method to conveniently build a paginated response.
     *
     * @param <T>           type of the elements.
     * @param content       list of elements on the page.
     * @param page          current page number (1-based).
     * @param size          page size.
     * @param isLast        {@code true} if there are no more pages.
     * @param totalPages    total number of available pages.
     * @param totalElements total number of unpaged elements.
     * @return new instance of {@link PaginatedResponse}.
     */
    public static <T> PaginatedResponse<T> from(List<T> content, Integer page, Integer size,
                                                Boolean isLast, Integer totalPages, Long totalElements)
    {
        return new PaginatedResponse<>(content, page, size, isLast, totalPages, totalElements);
    }
}
