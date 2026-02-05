package com.terridb.libris.dtos.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AuthorRequestDto {
    @Size(message = "Name must be between 1 and 150 characters", min = 1, max = 150)
    @NotBlank(message = "Name is required")
    private String fullName;
}
