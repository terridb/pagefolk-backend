package com.terridb.libris.dtos.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
public class BookRequestDto {
    @Size(message = "Title must be between 1 and 150 characters", min = 1, max = 150)
    @NotBlank(message = "Title is required")
    private String title;

    @Pattern(message = "ISBN must contain only digits and be 10 or 13 characters", regexp = "^(?:\\d{9}[\\dX]|\\d{13})$\n")
    @NotBlank(message = "ISBN is required")
    private String isbn;

    private LocalDate releaseDate;

    @Size(message = "Description must be between 10 and 5000 characters", min = 10, max = 5000)
    private String description;

    private Set<Long> authorIds;
    private Set<Long> genreIds;
    private Long seriesId;
}