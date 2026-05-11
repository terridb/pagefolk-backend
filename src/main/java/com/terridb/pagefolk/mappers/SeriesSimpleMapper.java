package com.terridb.pagefolk.mappers;

import com.terridb.pagefolk.dtos.bookseries.BookSeriesSimpleResponseDto;
import com.terridb.pagefolk.entities.BookSeriesEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SeriesSimpleMapper {

    public BookSeriesSimpleResponseDto toDto(BookSeriesEntity entity) {
        if (entity == null) {
            return null;
        }

        var result = new BookSeriesSimpleResponseDto();
        result.setId(entity.getId());
        result.setTitle(entity.getTitle());

        return result;
    }

    public List<BookSeriesSimpleResponseDto> toDto(List<BookSeriesEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        var result = new ArrayList<BookSeriesSimpleResponseDto>();
        for (BookSeriesEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }
}
