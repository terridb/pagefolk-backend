package com.terridb.pagefolk.dtos.list;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ListUpdateDto {
    @Size(min = 1, max = 150, message = "Name must be between 1 and 150 characters")
    private String name;

    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;
}
