package com.terridb.pagefolk.dtos.list;

import com.terridb.pagefolk.dtos.book.BookSimpleResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListResponseDto {
    private Long id;
    private String name;
    private String description;
    private List<BookSimpleResponseDto> books;
}
