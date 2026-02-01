package com.terridb.libris.mappers;

import com.terridb.libris.dtos.author.AuthorRequestDto;
import com.terridb.libris.dtos.author.AuthorResponseDto;
import com.terridb.libris.entities.AuthorEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthorMapper {

    public AuthorEntity toEntity(AuthorRequestDto requestDto, AuthorEntity author) {
        if (author == null) {
            author = new AuthorEntity();
        }

        author.setFullName(requestDto.getFullName());

        return author;
    }

    public AuthorResponseDto toDto(AuthorEntity entity) {
        var result = new AuthorResponseDto();
        result.setId(entity.getId());
        result.setFullName(entity.getFullName());

        return result;
    }

    public List<AuthorResponseDto> toDto(List<AuthorEntity> entities) {
        var result = new ArrayList<AuthorResponseDto>();
        for (AuthorEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }
}
