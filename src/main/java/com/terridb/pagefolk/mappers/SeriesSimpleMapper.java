package com.terridb.pagefolk.mappers;

import com.terridb.pagefolk.dtos.bookseries.BookSeriesSimpleResponseDto;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.BookSeriesEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class SeriesSimpleMapper {

    public BookSeriesSimpleResponseDto toDto(BookSeriesEntity entity) {
        if (entity == null) {
            return null;
        }

        var result = new BookSeriesSimpleResponseDto();
        result.setId(entity.getId());
        result.setTitle(entity.getTitle());
        result.setSeriesTotal(
                entity.getBooks().stream()
                        .filter(b -> b.getDeletedDate() == null)
                        .map(BookEntity::getSeriesOrder)
                        .filter(Objects::nonNull)
                        .max(Comparator.naturalOrder())
                        .orElse(BigDecimal.ZERO)
        );

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
