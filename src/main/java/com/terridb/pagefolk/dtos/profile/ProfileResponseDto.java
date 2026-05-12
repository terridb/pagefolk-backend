package com.terridb.pagefolk.dtos.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponseDto {
    private Long id;
    private String profilePictureUrl;
    private String bio;
}
