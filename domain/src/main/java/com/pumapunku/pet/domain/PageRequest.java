package com.pumapunku.pet.domain;

/**
 * Domain object that encapsulates the parameters of a pagination request.
 *
 * <p>Uses <strong>1-based</strong> page numbering (first page is 1),
 * which simplifies usage from the presentation layer and REST APIs.</p>
 *
 * <p>Conversion to 0-based indexes (required by Spring Data or other
 * infrastructure libraries) is done via {@link #zeroBasedPage()}.</p>
 *
 * @param page requested page number (1-based; minimum 1).
 * @param size maximum number of elements per page.
 */
public record PageRequest(int page, int size)
{
    /**
     * Default page number when none is specified.
     */
    public static final int DEFAULT_PAGE = 1;

    /**
     * Default page size when none is specified.
     */
    public static final int DEFAULT_SIZE = 200;

    /**
     * Calculates the offset for the database query.
     *
     * @return index of the first element on this page.
     */
    public int offset()
    {
        return (page - 1) * size;
    }

    /**
     * Returns the 0-based page number, for use with Spring Data / JPA.
     *
     * @return {@code page - 1}.
     */
    public int zeroBasedPage()
    {
        return page - 1;
    }

    /**
     * Creates a {@link PageRequest} with default values
     * ({@link #DEFAULT_PAGE} and {@link #DEFAULT_SIZE}).
     *
     * @return instance with page 1 and size 200.
     */
    public static PageRequest ofDefaults()
    {
        return new PageRequest(DEFAULT_PAGE, DEFAULT_SIZE);
    }
}
