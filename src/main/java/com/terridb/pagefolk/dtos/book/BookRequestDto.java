package com.terridb.pagefolk.dtos.book;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class BookRequestDto {
    @Size(message = "Title must be between 1 and 150 characters", min = 1, max = 150)
    @NotBlank(message = "Title is required")
    private String title;

    @Pattern(message = "ISBN must contain only digits and be 10 or 13 characters", regexp = "^(?:\\d{9}[\\dX]|\\d{13})$")
    @NotBlank(message = "ISBN is required")
    private String isbn;

    private LocalDate releaseDate;

    @Size(message = "Description must be between 10 and 5000 characters", min = 10, max = 5000)
    private String description;

    @NotEmpty(message = "At least one author must be provided")
    private Set<
            @NotNull(message = "Author id is required")
            @Positive(message = "Author id must be a positive number")
                    Long> authorIds;

    @NotEmpty(message = "At least one genre must be provided")
    private Set<
            @NotNull(message = "Genre id is required")
            @Positive(message = "Genre id must be a positive number")
                    Long> genreIds;

    @Positive(message = "Series id must be a positive number")
    private Long seriesId;

    @DecimalMin(value = "0.1", message = "Series order must be at least 0.1")
    @Digits(integer = 4, fraction = 1, message = "Series order must have at most 1 decimal place")
    private BigDecimal seriesOrder;
}