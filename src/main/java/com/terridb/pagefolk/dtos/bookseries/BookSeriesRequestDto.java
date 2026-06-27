package com.terridb.pagefolk.dtos.bookseries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class BookSeriesRequestDto {
    @Size(message = "Title must be between 1 and 150 characters", min = 1, max = 150)
    @NotBlank(message = "Title is required")
    private String title;

    @Size(message = "Description must be between 10 and 5000 characters", min = 10, max = 5000)
    private String description;

    private Set<Long> bookIds;
}
