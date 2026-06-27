package com.terridb.pagefolk.dtos.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthorRequestDto {
    @Size(message = "Name must be between 1 and 150 characters", min = 1, max = 150)
    @NotBlank(message = "Name is required")
    private String fullName;
}
