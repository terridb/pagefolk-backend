package com.terridb.pagefolk.dtos.bookseries;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BookSeriesSimpleResponseDto {
    private Long id;
    private String title;
    private BigDecimal seriesTotal;
}
