package com.pumapunku.pet.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PageRequest
{
    private Integer page;
    private Integer size;

    public Integer offset()
    {
        return page * size;
    }
}
