package com.terridb.pagefolk.mappers;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.rating.RatingRequestDto;
import com.terridb.pagefolk.dtos.rating.RatingResponseDto;
import com.terridb.pagefolk.dtos.rating.RatingUpdateRequestDto;
import com.terridb.pagefolk.entities.RatingEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RatingMapper {
    private static final BookSimpleMapper bookSimpleMapper = new BookSimpleMapper();
    private static final UserSimpleMapper userSimpleMapper = new UserSimpleMapper();

    public RatingEntity toEntity(RatingRequestDto requestDto, RatingEntity rating) {
        if (rating == null) {
            rating = new RatingEntity();
        }

        rating.setScore(requestDto.getScore());
        rating.setReview(requestDto.getReview());

        return rating;
    }

    public RatingEntity toEntity(RatingUpdateRequestDto requestDto, RatingEntity rating) {
        rating.setScore(requestDto.getScore());
        rating.setReview(requestDto.getReview());
        return rating;
    }

    public RatingResponseDto toDto(RatingEntity entity) {
        var result = new RatingResponseDto();
        result.setId(entity.getId());
        result.setScore(entity.getScore());
        result.setReview(entity.getReview());
        result.setBook(bookSimpleMapper.toDto(entity.getBook()));
        result.setUser(userSimpleMapper.toDto(entity.getUser()));

        return result;
    }

    public List<RatingResponseDto> toDto(List<RatingEntity> entities) {
        var result = new ArrayList<RatingResponseDto>();
        for (RatingEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }

    public PaginatedResponse<RatingResponseDto> toPaginatedDto(Page<RatingEntity> entitiesPage) {
        PaginatedResponse<RatingResponseDto> response = new PaginatedResponse<>();
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
