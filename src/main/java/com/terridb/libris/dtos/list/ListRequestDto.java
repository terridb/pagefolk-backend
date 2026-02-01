package com.terridb.libris.dtos.list;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ListRequestDto {
    @Size(message = "Name must be between 1 and 150 characters", min = 1, max = 150)
    @NotBlank(message = "Name is required")
    private String name;

    @Size(message = "Description must be between 10 and 5000 characters", min = 10, max = 5000)
    private String description;

    @NotNull(message = "Profile id is required")
    private Long profileId;
}
