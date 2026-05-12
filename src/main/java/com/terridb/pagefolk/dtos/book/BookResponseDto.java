package com.terridb.pagefolk.dtos.book;

import com.terridb.pagefolk.dtos.author.AuthorResponseDto;
import com.terridb.pagefolk.dtos.bookseries.BookSeriesSimpleResponseDto;
import com.terridb.pagefolk.dtos.genre.GenreResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookResponseDto {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate releaseDate;
    private String description;
    private String imageUrl;
    private BigDecimal averageRating;
    private int ratingCount;
    private BigDecimal seriesOrder;
    private List<AuthorResponseDto> authors;
    private BookSeriesSimpleResponseDto series;
    private List<GenreResponseDto> genres;
}
