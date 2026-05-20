package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.rating.RatingFilterDto;
import com.terridb.pagefolk.dtos.rating.RatingRequestDto;
import com.terridb.pagefolk.dtos.rating.RatingUpdateRequestDto;
import com.terridb.pagefolk.dtos.rating.RatingResponseDto;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.RatingEntity;
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
import org.springframework.transaction.annotation.Transactional;

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
        UserEntity user = findUserById(userId);
        return ratingMapper.toPaginatedDto(ratingRepository.findAllByUser(user, buildPageable(filter)));
    }

    public PaginatedResponse<RatingResponseDto> getRatingsByBookId(Long bookId, RatingFilterDto filter) {
        BookEntity book = findBookById(bookId);
        return ratingMapper.toPaginatedDto(ratingRepository.findAllByBook(book, buildPageable(filter)));
    }

    @Transactional
    public RatingResponseDto postRating(RatingRequestDto input) {
        RatingEntity entity = ratingMapper.toEntity(input, null);
        BookEntity book = findBookById(input.getBookId());
        UserEntity user = findUserById(input.getUserId());

        entity.setBook(book);
        entity.setUser(user);

        RatingEntity savedEntity = ratingRepository.save(entity);
        updateBookStats(book);
        return ratingMapper.toDto(savedEntity);
    }

    @Transactional
    public RatingResponseDto updateRating(Long id, RatingUpdateRequestDto input) {
        RatingEntity entity = ratingRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Rating with id: " + id + " not found"));

        RatingEntity savedEntity = ratingRepository.save(ratingMapper.toEntity(input, entity));
        updateBookStats(entity.getBook());
        return ratingMapper.toDto(savedEntity);
    }

    @Transactional
    public void deleteRating(Long id) {
        RatingEntity entity = ratingRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Rating with id: " + id + " not found"));

        BookEntity book = entity.getBook();
        ratingRepository.delete(entity);
        updateBookStats(book);
    }

    private void updateBookStats(BookEntity book) {
        book.setAverageRating(ratingRepository.calculateAverageRatingByBook(book));
        book.setRatingCount((int) ratingRepository.countByBook(book));
        bookRepository.save(book);
    }

    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User with id: " + userId + " not found"));
    }

    private BookEntity findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book with id: " + bookId + " not found"));
    }

    private Pageable buildPageable(RatingFilterDto filter) {
        int page = Math.max(filter.getPage(), 0);
        int size = (filter.getSize() <= 0 || filter.getSize() > 100) ? 20 : filter.getSize();
        return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(filter.getDirection()), filter.getSortBy()));
    }
}
