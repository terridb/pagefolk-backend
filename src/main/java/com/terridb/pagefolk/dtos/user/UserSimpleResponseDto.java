package com.terridb.pagefolk.dtos.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSimpleResponseDto {
    private String id;
    private String username;
    private String email;
}
