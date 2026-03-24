package com.terridb.libris.mappers;

import com.terridb.libris.dtos.book.BookRequestDto;
import com.terridb.libris.dtos.book.BookResponseDto;
import com.terridb.libris.dtos.book.PaginatedBookResponse;
import com.terridb.libris.entities.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookMapper {

    private static final AuthorMapper authorMapper = new AuthorMapper();
    private static final GenreMapper genreMapper = new GenreMapper();
    private static final SeriesSimpleMapper seriesSimpleMapper = new SeriesSimpleMapper();

    public BookEntity toEntity(BookRequestDto requestDto, BookEntity book) {
        if (book == null) {
            book = new BookEntity();
        }

        book.setTitle(requestDto.getTitle());
        book.setIsbn(requestDto.getIsbn());
        book.setReleaseDate(requestDto.getReleaseDate());
        book.setDescription(requestDto.getDescription());

        return book;
    }

    public BookResponseDto toDto(BookEntity entity) {
        var result = new BookResponseDto();
        result.setId(entity.getId());
        result.setTitle(entity.getTitle());
        result.setIsbn(entity.getIsbn());
        result.setReleaseDate(entity.getReleaseDate());
        result.setDescription(entity.getDescription());
        result.setImageUrl(entity.getImageUrl());
        result.setAverageRating(entity.getAverageRating());
        result.setRatingCount(entity.getRatingCount());
        result.setAuthors(
                entity.getAuthors()
                        .stream()
                        .map(authorMapper::toDto)
                        .toList()
        );
        result.setGenres(
                entity.getGenres()
                        .stream()
                        .map(genreMapper::toDto)
                        .toList()
        );
        result.setSeries(seriesSimpleMapper.toDto(entity.getSeries()));

        return result;
    }

    public List<BookResponseDto> toDto(List<BookEntity> entities) {
        var result = new ArrayList<BookResponseDto>();
        for (BookEntity entity : entities) {
            result.add(toDto(entity));
        }
        return result;
    }

    public PaginatedBookResponse toPaginatedDto(Page<BookEntity> entitiesPage) {
        PaginatedBookResponse response = new PaginatedBookResponse();
        response.setContent(toDto(entitiesPage.getContent()));
        response.setPage(entitiesPage.getNumber());
        response.setSize(entitiesPage.getSize());
        response.setTotalElements(entitiesPage.getTotalElements());
        response.setTotalPages(entitiesPage.getTotalPages());
        return response;
    }
}
