package com.terridb.libris.dtos.list;

import com.terridb.libris.dtos.book.BookSimpleResponseDto;
import lombok.Setter;

import java.util.List;

@Setter
public class ListResponseDto {
    private Long id;
    private String name;
    private String description;
    private List<BookSimpleResponseDto> books;
}
