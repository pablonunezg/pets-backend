package com.pumapunku.pet.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Page<T>
{
    private List<T> content;
    private Integer pageNumber;
    private Integer pageSize;

    public Boolean isLast()
    {
        return pageNumber + 1 >= totalPages();
    }

    public Integer totalPages()
    {
        if (content.isEmpty())
        {
            return 1;
        }
        else
        {
            return Double.valueOf(Math.ceil(content.size() / (double) pageSize)).intValue();
        }
    }

    public Integer size()
    {
        return content.size();
    }
}
