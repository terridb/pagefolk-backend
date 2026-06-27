package com.terridb.pagefolk.dtos.rating;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RatingUpdateRequestDto {
    @DecimalMin(value = "0.0", message = "Score must be between 0.0 and 5.0")
    @DecimalMax(value = "5.0", message = "Score must be between 0.0 and 5.0")
    @NotNull(message = "Score is required")
    private BigDecimal score;

    @Size(message = "Review must be between 10 and 5000 characters", min = 10, max = 5000)
    private String review;
}
