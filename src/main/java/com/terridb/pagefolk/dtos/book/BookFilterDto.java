package com.terridb.pagefolk.dtos.book;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookFilterDto {
    private String search;
    private List<Long> authorIds;
    private List<String> genres;
    private Long seriesId;
    private Double minRating;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseAfter;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseBefore;
    private String sortBy = "title";
    private String direction = "ASC";
    private int page = 0;
    private int size = 20;
}
