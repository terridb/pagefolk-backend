package com.terridb.pagefolk.mappers;

import com.terridb.pagefolk.dtos.rating.RatingRequestDto;
import com.terridb.pagefolk.dtos.rating.RatingResponseDto;
import com.terridb.pagefolk.entities.RatingEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RatingMapper {
    private static final BookSimpleMapper bookSimpleMapper = new BookSimpleMapper();
    private static final ProfileMapper profileMapper = new ProfileMapper();

    public RatingEntity toEntity(RatingRequestDto requestDto, RatingEntity rating) {
        if (rating == null) {
            rating = new RatingEntity();
        }

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
        result.setProfile(profileMapper.toDto(entity.getUser().getProfile()));

        return result;
    }

    public List<RatingResponseDto> toDto(List<RatingEntity> entities) {
        var result = new ArrayList<RatingResponseDto>();
        for (RatingEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }
}
