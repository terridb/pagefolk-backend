package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.rating.RatingFilterDto;
import com.terridb.pagefolk.dtos.rating.RatingResponseDto;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.UserEntity;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.RatingMapper;
import com.terridb.pagefolk.repositories.BookRepository;
import com.terridb.pagefolk.repositories.RatingRepository;
import com.terridb.pagefolk.repositories.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final RatingMapper ratingMapper;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public RatingService(RatingMapper ratingMapper, RatingRepository ratingRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.ratingMapper = ratingMapper;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public RatingResponseDto getRatingById(Long id) {
        return ratingMapper.toDto(
                ratingRepository.findById(id)
                        .orElseThrow(() -> new RecordNotFoundException("Rating with id: " + id + " not found"))
        );
    }

    public PaginatedResponse<RatingResponseDto> getAllRatings(RatingFilterDto filter) {
        return ratingMapper.toPaginatedDto(ratingRepository.findAll(buildPageable(filter)));
    }

    public PaginatedResponse<RatingResponseDto> getRatingsByUserId(Long userId, RatingFilterDto filter) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User with id: " + userId + " not found"));

        return ratingMapper.toPaginatedDto(ratingRepository.findAllByUser(user, buildPageable(filter)));
    }

    public PaginatedResponse<RatingResponseDto> getRatingsByBookId(Long bookId, RatingFilterDto filter) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book with id: " + bookId + " not found"));

        return ratingMapper.toPaginatedDto(ratingRepository.findAllByBook(book, buildPageable(filter)));
    }

    private Pageable buildPageable(RatingFilterDto filter) {
        int page = Math.max(filter.getPage(), 0);
        int size = (filter.getSize() <= 0 || filter.getSize() > 100) ? 20 : filter.getSize();
        return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(filter.getDirection()), filter.getSortBy()));
    }
}
