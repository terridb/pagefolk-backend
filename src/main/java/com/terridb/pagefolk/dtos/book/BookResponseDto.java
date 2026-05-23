package com.terridb.pagefolk.dtos.book;

import com.terridb.pagefolk.dtos.bookseries.BookSeriesSimpleResponseDto;
import com.terridb.pagefolk.dtos.genre.GenreResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookResponseDto extends BookSimpleResponseDto {
    private String isbn;
    private LocalDate releaseDate;
    private String description;
    private BigDecimal seriesOrder;
    private BookSeriesSimpleResponseDto series;
    private List<GenreResponseDto> genres;
}
