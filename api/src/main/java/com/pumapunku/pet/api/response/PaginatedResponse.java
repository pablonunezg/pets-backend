package com.pumapunku.pet.api.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PaginatedResponse<T>
{
    private List<T> content;
    private Integer page;
    private Integer size;
    private Boolean isLast;
    private Integer totalPages;

    public static <T> PaginatedResponse<T> from(List<T> content, Integer page, Integer size, Boolean isLast, Integer totalPages)
    {
        return new PaginatedResponse<>(content, page, size, isLast, totalPages);
    }
}
