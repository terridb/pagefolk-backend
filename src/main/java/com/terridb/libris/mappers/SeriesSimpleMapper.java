package com.terridb.libris.mappers;

import com.terridb.libris.dtos.bookseries.BookSeriesSimpleResponseDto;
import com.terridb.libris.entities.BookSeriesEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SeriesSimpleMapper {

    public BookSeriesSimpleResponseDto toDto(BookSeriesEntity entity) {
        var result = new BookSeriesSimpleResponseDto();
        result.setId(entity.getId());
        result.setTitle(entity.getTitle());

        return result;
    }

    public List<BookSeriesSimpleResponseDto> toDto(List<BookSeriesEntity> entities) {
        var result = new ArrayList<BookSeriesSimpleResponseDto>();
        for (BookSeriesEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }
}
