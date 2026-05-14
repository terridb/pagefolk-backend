package com.terridb.pagefolk.dtos.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAdminResponseDto {
    private String id;
    private String kcid;
    private String createdDate;
    private String editedDate;
    private String username;
    private String email;
    private String deletedDate;
}
