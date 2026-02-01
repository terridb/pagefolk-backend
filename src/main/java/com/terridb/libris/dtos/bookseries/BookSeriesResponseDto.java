package com.terridb.libris.dtos.bookseries;

import com.terridb.libris.dtos.book.BookSimpleResponseDto;
import lombok.Setter;

import java.util.List;

@Setter
public class BookSeriesResponseDto {
    private Long id;
    private String title;
    private String description;
    private List<BookSimpleResponseDto> books;
}
