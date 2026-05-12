package com.terridb.pagefolk.dtos.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterDto {
    private String search;
    private Boolean deleted;
    private String sortBy = "username";
    private String direction = "ASC";
    private int page = 0;
    private int size = 20;
}
