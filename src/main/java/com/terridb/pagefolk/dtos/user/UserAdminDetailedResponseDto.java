package com.terridb.pagefolk.dtos.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserAdminDetailedResponseDto extends UserAdminResponseDto {
    private String bio;
    private String profilePictureUrl;
    private List<Long> listIds;
}
