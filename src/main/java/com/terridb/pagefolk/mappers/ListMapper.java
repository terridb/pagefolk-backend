package com.terridb.pagefolk.mappers;

import com.terridb.pagefolk.dtos.list.ListRequestDto;
import com.terridb.pagefolk.dtos.list.ListResponseDto;
import com.terridb.pagefolk.entities.ListEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ListMapper {
    private static final BookSimpleMapper bookSimpleMapper = new BookSimpleMapper();

    public ListEntity toEntity(ListRequestDto requestDto, ListEntity list) {
        if (list == null) {
            list = new ListEntity();
        }

        list.setName(requestDto.getName());
        list.setDescription(requestDto.getDescription());

        return list;
    }

    public ListResponseDto toDto(ListEntity entity) {
        var result = new ListResponseDto();
        result.setId(entity.getId());
        result.setName(entity.getName());
        result.setDescription(entity.getDescription());
        result.setBooks(bookSimpleMapper.toDto
                        (entity.getBooks()
                                .stream()
                                .toList()));

        return result;
    }

    public List<ListResponseDto> toDto(List<ListEntity> entities) {
        var result = new ArrayList<ListResponseDto>();
        for (ListEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }
}
