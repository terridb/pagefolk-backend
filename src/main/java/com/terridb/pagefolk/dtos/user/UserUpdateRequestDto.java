package com.terridb.pagefolk.dtos.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {

    @Size(max = 50)
    private String username;

    @Size(max = 500)
    private String bio;
}
