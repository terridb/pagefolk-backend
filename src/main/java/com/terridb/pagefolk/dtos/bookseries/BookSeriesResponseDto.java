package com.terridb.pagefolk.dtos.bookseries;

import com.terridb.pagefolk.dtos.book.BookSimpleResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookSeriesResponseDto extends BookSimpleResponseDto {
    private String description;
    private List<BookSimpleResponseDto> books;
}
