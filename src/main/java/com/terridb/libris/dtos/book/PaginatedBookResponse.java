package com.terridb.libris.dtos.book;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedBookResponse {
    private List<BookResponseDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
}
//todo herschrijven naar herbruikbaar