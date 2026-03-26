package com.terridb.libris.dtos.author;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedAuthorResponse {
    private List<AuthorResponseDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
}
