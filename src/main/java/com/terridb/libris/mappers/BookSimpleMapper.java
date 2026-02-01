package com.terridb.libris.mappers;

import com.terridb.libris.dtos.book.BookSimpleResponseDto;
import com.terridb.libris.entities.BookEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookSimpleMapper {

    private static final AuthorMapper authorMapper = new AuthorMapper();

    public BookSimpleResponseDto toDto(BookEntity entity) {
        var result = new BookSimpleResponseDto();
        result.setId(entity.getId());
        result.setTitle(entity.getTitle());
        result.setImageUrl(entity.getImageUrl());
        result.setAverageRating(entity.getAverageRating());
        result.setRatingCount(entity.getRatingCount());
        result.setAuthors(
                entity.getAuthors()
                        .stream()
                        .map(authorMapper::toDto)
                        .toList()
        );

        return result;
    }

    public List<BookSimpleResponseDto> toDto(List<BookEntity> entities) {
        var result = new ArrayList<BookSimpleResponseDto>();
        for (BookEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }
}
