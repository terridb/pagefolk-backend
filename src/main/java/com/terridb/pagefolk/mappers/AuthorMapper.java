package com.terridb.pagefolk.mappers;

import com.terridb.pagefolk.dtos.author.AuthorRequestDto;
import com.terridb.pagefolk.dtos.author.AuthorResponseDto;
import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.entities.AuthorEntity;
import org.springframework.data.domain.Page;
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

    public PaginatedResponse<AuthorResponseDto> toPaginatedDto(Page<AuthorEntity> entitiesPage) {
        PaginatedResponse<AuthorResponseDto> response = new PaginatedResponse<>();
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
