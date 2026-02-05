package com.terridb.libris.mappers;

import com.terridb.libris.dtos.bookseries.BookSeriesRequestDto;
import com.terridb.libris.dtos.bookseries.BookSeriesResponseDto;
import com.terridb.libris.entities.BookSeriesEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SeriesMapper {

    private static final BookSimpleMapper bookSimpleMapper = new BookSimpleMapper();

    public BookSeriesEntity toEntity(BookSeriesRequestDto requestDto, BookSeriesEntity series) {
        if (series == null) {
            series = new BookSeriesEntity();
        }

        series.setTitle(requestDto.getTitle());
        series.setDescription(requestDto.getDescription());

        return series;
    }

    public BookSeriesResponseDto toDto(BookSeriesEntity entity) {
        var result = new BookSeriesResponseDto();
        result.setId(entity.getId());
        result.setTitle(entity.getTitle());
        result.setDescription(entity.getDescription());
        result.setBooks(
                entity.getBooks()
                        .stream()
                        .map(bookSimpleMapper::toDto)
                        .toList()
        );

        return result;
    }

    public List<BookSeriesResponseDto> toDto(List<BookSeriesEntity> entities) {
        var result = new ArrayList<BookSeriesResponseDto>();
        for (BookSeriesEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }
}
