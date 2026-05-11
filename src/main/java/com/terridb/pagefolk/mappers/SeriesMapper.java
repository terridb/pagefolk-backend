package com.terridb.pagefolk.mappers;

import com.terridb.pagefolk.dtos.bookseries.BookSeriesRequestDto;
import com.terridb.pagefolk.dtos.bookseries.BookSeriesResponseDto;
import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.entities.BookSeriesEntity;
import org.springframework.data.domain.Page;
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

    public PaginatedResponse<BookSeriesResponseDto> toPaginatedDto(Page<BookSeriesEntity> entitiesPage) {
        PaginatedResponse<BookSeriesResponseDto> response = new PaginatedResponse<>();
        response.setContent(toDto(entitiesPage.getContent()));
        response.setPage(entitiesPage.getNumber());
        response.setSize(entitiesPage.getSize());
        response.setTotalElements(entitiesPage.getTotalElements());
        response.setTotalPages(entitiesPage.getTotalPages());
        response.setHasNextPage(response.getPage() + 1 < response.getTotalPages());
        response.setHasPreviousPage(response.getPage() > 0);
        return response;
    }
}
