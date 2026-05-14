package com.terridb.pagefolk.mappers;

import com.terridb.pagefolk.dtos.user.UserSimpleResponseDto;
import com.terridb.pagefolk.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserSimpleMapper {

    public UserSimpleResponseDto toDto(UserEntity entity) {
        var result = new UserSimpleResponseDto();
        result.setId(entity.getId().toString());
        result.setUsername(entity.getUsername());
        result.setEmail(entity.getEmail());
        return result;
    }
}
