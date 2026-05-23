package com.terridb.pagefolk.dtos.rating;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingFilterDto {
    private String sortBy = "createdDate";
    private String direction = "DESC";
    private int page = 0;
    private int size = 20;
}
