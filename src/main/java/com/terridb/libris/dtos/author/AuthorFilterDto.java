package com.terridb.libris.dtos.author;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorFilterDto {
    private String search;
    private String sortBy = "fullName";
    private String direction = "ASC";
    private int page = 0;
    private int size = 20;
}
