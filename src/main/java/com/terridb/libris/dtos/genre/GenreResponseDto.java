package com.terridb.libris.dtos.genre;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreResponseDto {
    private Long id;
    private String name;
    private String description;
}
