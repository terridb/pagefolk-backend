package com.terridb.pagefolk.dtos.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserAdminDetailedResponseDto {
    private String id;
    private String kcid;
    private String createdDate;
    private String editedDate;
    private String username;
    private String email;
    private String deletedDate;
    private String bio;
    private String profilePictureUrl;
    private List<Long> listIds;
}
