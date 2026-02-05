package com.terridb.libris.dtos.profile;

import lombok.Setter;

@Setter
public class ProfileResponseDto {
    private Long id;
    private String imageUrl;
    private boolean deleted;
}
