package com.terridb.pagefolk.dtos.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailedResponseDto extends UserSimpleResponseDto {
    private String bio;
    private List<Long> listIds;
}
