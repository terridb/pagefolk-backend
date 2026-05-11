package com.terridb.pagefolk.dtos.rating;

import com.terridb.pagefolk.dtos.book.BookSimpleResponseDto;
import com.terridb.pagefolk.dtos.profile.ProfileResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RatingResponseDto {
    private Long id;
    private BigDecimal score;
    private String review;
    private BookSimpleResponseDto book;
    private ProfileResponseDto profile;
}
