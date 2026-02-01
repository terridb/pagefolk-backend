package com.terridb.libris.mappers;

import com.terridb.libris.dtos.author.AuthorRequestDto;
import com.terridb.libris.dtos.author.AuthorResponseDto;
import com.terridb.libris.dtos.genre.GenreRequestDto;
import com.terridb.libris.dtos.genre.GenreResponseDto;
import com.terridb.libris.entities.AuthorEntity;
import com.terridb.libris.entities.GenreEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GenreMapper {

    public GenreEntity toEntity(GenreRequestDto requestDto, GenreEntity genre) {
        if (genre == null) {
            genre = new GenreEntity();
        }

        genre.setName(requestDto.getName());
        genre.setDescription(requestDto.getDescription());

        return genre;
    }

    public GenreResponseDto toDto(GenreEntity entity) {
        var result = new GenreResponseDto();
        result.setId(entity.getId());
        result.setName(entity.getName());
        result.setDescription(entity.getDescription());

        return result;
    }

    public List<GenreResponseDto> toDto(List<GenreEntity> entities) {
        var result = new ArrayList<GenreResponseDto>();
        for (GenreEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }
}
