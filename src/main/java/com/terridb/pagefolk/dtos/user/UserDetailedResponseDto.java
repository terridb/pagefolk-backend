package com.terridb.pagefolk.dtos.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailedResponseDto {
    private String id;
    private String username;
    private String email;
    private String bio;
    private String profilePictureUrl;
    private List<Long> listIds;
}
