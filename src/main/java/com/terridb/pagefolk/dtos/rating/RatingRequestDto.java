package com.terridb.pagefolk.dtos.rating;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class RatingRequestDto {
    @DecimalMin(value = "0.0", message = "Score must be between 0.0 and 5.0")
    @DecimalMax(value = "5.0", message = "Score must be between 0.0 and 5.0")
    @NotNull(message = "Score is required")
    private BigDecimal score;

    @Size(message = "Review must be between 10 and 5000 characters", min = 10, max = 5000)
    private String review;

    @NotNull(message = "Book id is required")
    @Positive(message = "Book id must be positive")
    private Long bookId;

    @NotNull(message = "Profile id is required")
    @Positive(message = "Profile id must be positive")
    private Long profileId;
}
