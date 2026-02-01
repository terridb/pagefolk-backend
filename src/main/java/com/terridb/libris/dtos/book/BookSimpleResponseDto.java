package com.terridb.libris.dtos.book;

import com.terridb.libris.dtos.author.AuthorResponseDto;
import com.terridb.libris.entities.BookSeriesEntity;
import com.terridb.libris.entities.GenreEntity;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Setter
public class BookSimpleResponseDto {
    private Long id;
    private String title;
    private String imageUrl;
    private BigDecimal averageRating;
    private int ratingCount;
    private Set<AuthorResponseDto> authors;
}
