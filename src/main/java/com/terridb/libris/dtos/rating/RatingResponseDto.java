package com.terridb.libris.dtos.rating;

import com.terridb.libris.dtos.book.BookSimpleResponseDto;
import com.terridb.libris.dtos.profile.ProfileResponseDto;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
public class RatingResponseDto {
    private Long id;
    private BigDecimal score;
    private String review;
    private BookSimpleResponseDto book;
    private ProfileResponseDto profile;
}
