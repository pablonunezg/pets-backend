package com.pumapunku.pet.presentation.util;

import com.pumapunku.pet.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Generic utilities for building HTTP responses in the presentation layer.
 *
 * <p>Centralizes the paginated response pattern, avoiding duplicate logic
 * in each controller. The total element count is communicated to the client
 * via the standard {@code X-Total-Count} header.</p>
 *
 * <p>Usage in a controller:</p>
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
    /**
     * HTTP header that exposes the total available elements without pagination.
     */
    public static final String HEADER_TOTAL_COUNT = "X-Total-Count";

    private ResponseUtils()
    {
        // Utility class: not instantiable
    }

    /**
     * Builds a {@code 200 OK} response with the list of elements and the
     * {@value #HEADER_TOTAL_COUNT} header obtained from the domain {@link Page}.
     *
     * @param <T>     type of the body elements.
     * @param content list of elements to return in the body.
     * @param page    domain page from which {@code totalElements} is extracted.
     * @return {@link ResponseEntity} with status 200, body {@code content} and
     * header {@code X-Total-Count: <totalElements>}.
     */
    public static <T> ResponseEntity<List<T>> paginatedOk(List<T> content, Page<?> page)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_TOTAL_COUNT, String.valueOf(page.totalElements()));
        return ResponseEntity.ok().headers(headers).body(content);
    }
}
