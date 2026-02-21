package com.pumapunku.pet.domain;

/**
 * Objeto de dominio que encapsula los parámetros de una solicitud de paginación.
 *
 * <p>Utiliza numeración de páginas <strong>basada en 1</strong> (la primera página es la 1),
 * lo que facilita el uso desde la capa de presentación y las APIs REST.</p>
 *
 * <p>La conversión a índices basados en 0 (requerida por Spring Data u otras
 * librerías de infraestructura) se realiza mediante {@link #zeroBasedPage()}.</p>
 *
 * @param page número de página solicitado (basado en 1; mínimo 1).
 * @param size número máximo de elementos por página.
 */
public record PageRequest(int page, int size)
{
    /** Página por defecto si no se especifica. */
    public static final int DEFAULT_PAGE = 1;

    /** Tamaño de página por defecto si no se especifica. */
    public static final int DEFAULT_SIZE = 200;

    /**
     * Calcula el desplazamiento (offset) para la consulta de base de datos.
     *
     * @return índice del primer elemento de esta página.
     */
    public int offset()
    {
        return (page - 1) * size;
    }

    /**
     * Devuelve el número de página basado en 0, para uso en Spring Data / JPA.
     *
     * @return {@code page - 1}.
     */
    public int zeroBasedPage()
    {
        return page - 1;
    }

    /**
     * Crea un {@link PageRequest} con los valores por defecto
     * ({@link #DEFAULT_PAGE} y {@link #DEFAULT_SIZE}).
     *
     * @return instancia con página 1 y tamaño 200.
     */
    public static PageRequest ofDefaults()
    {
        return new PageRequest(DEFAULT_PAGE, DEFAULT_SIZE);
    }
}
