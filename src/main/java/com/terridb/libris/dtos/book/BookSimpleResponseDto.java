package com.terridb.libris.dtos.book;

import com.terridb.libris.dtos.author.AuthorResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BookSimpleResponseDto {
    private Long id;
    private String title;
    private String imageUrl;
    private BigDecimal averageRating;
    private int ratingCount;
    private List<AuthorResponseDto> authors;
}
