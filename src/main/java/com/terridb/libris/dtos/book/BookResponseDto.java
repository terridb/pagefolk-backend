package com.terridb.libris.dtos.book;

import com.terridb.libris.dtos.author.AuthorResponseDto;
import com.terridb.libris.dtos.bookseries.BookSeriesSimpleResponseDto;
import com.terridb.libris.dtos.genre.GenreResponseDto;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    private List<AuthorResponseDto> authors;
    private BookSeriesSimpleResponseDto series;
    private List<GenreResponseDto> genres;
}
