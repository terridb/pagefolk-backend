package com.terridb.pagefolk.dtos.book;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class BookPatchDto {
    @Size(message = "Title must be between 1 and 150 characters", min = 1, max = 150)
    private String title;

    @Pattern(message = "ISBN must contain only digits and be 10 or 13 characters", regexp = "^(?:\\d{9}[\\dX]|\\d{13})$")
    private String isbn;

    private LocalDate releaseDate;

    @Size(message = "Description must be between 10 and 5000 characters", min = 10, max = 5000)
    private String description;

    @DecimalMin(value = "0.1", message = "Series order must be at least 0.1")
    @Digits(integer = 4, fraction = 1, message = "Series order must have at most 1 decimal place")
    private BigDecimal seriesOrder;
}
