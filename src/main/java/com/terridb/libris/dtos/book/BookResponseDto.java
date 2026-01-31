package com.terridb.libris.dtos.book;

import com.terridb.libris.dtos.author.AuthorResponseDto;
import com.terridb.libris.dtos.bookseries.BookSeriesSimpleResponseDto;
import com.terridb.libris.dtos.genre.GenreResponseDto;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Setter
public class BookResponseDto {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate releaseDate;
    private String description;
    private String imageUrl;
    private int averageRating;
    private Set<AuthorResponseDto> authors;
    private BookSeriesSimpleResponseDto series;
    private Set<GenreResponseDto> genres;
}
