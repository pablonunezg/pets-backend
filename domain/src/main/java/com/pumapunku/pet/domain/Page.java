package com.pumapunku.pet.domain;

import java.util.List;

/**
 * Generic container for paginated domain results.
 *
 * <p>Wraps a page of results together with pagination metadata,
 * with no dependencies on Spring Data or any infrastructure library.</p>
 *
 * <p>Uses <strong>1-based</strong> page numbering
 * (first page is 1), consistent with {@link PageRequest}.</p>
 *
 * @param <T>           type of the elements contained in the page.
 * @param content       list of elements on the current page.
 * @param pageNumber    current page number (1-based).
 * @param pageSize      maximum number of elements per page.
 * @param totalElements total elements that satisfy the query.
 */
public record Page<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements
)
{
    /**
     * Calculates the total number of available pages.
     *
     * @return total pages; at least 1 even when there are no elements.
     */
    public int totalPages()
    {
        return pageSize == 0 ? 1 : (int) Math.ceil((double) totalElements / pageSize);
    }

    /**
     * Indicates whether this is the last available page.
     *
     * <p>Since numbering is 1-based, the last page is reached
     * when {@code pageNumber >= totalPages()}.</p>
     *
     * @return {@code true} if there are no more pages after this one.
     */
    public boolean isLast()
    {
        return pageNumber >= totalPages();
    }
}
