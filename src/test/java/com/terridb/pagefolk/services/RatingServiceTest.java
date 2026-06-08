package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.rating.RatingFilterDto;
import com.terridb.pagefolk.dtos.rating.RatingRequestDto;
import com.terridb.pagefolk.dtos.rating.RatingResponseDto;
import com.terridb.pagefolk.dtos.rating.RatingUpdateRequestDto;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.RatingEntity;
import com.terridb.pagefolk.entities.UserEntity;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.RatingMapper;
import com.terridb.pagefolk.repositories.BookRepository;
import com.terridb.pagefolk.repositories.RatingRepository;
import com.terridb.pagefolk.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingMapper ratingMapper;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private RatingService ratingService;

    RatingEntity rating;
    RatingResponseDto ratingDto;
    BookEntity book;
    UserEntity user;
    Page<RatingEntity> ratingsPage;
    PaginatedResponse<RatingResponseDto> paginatedResponse;

    @BeforeEach
    void setUp() {
        book = new BookEntity();
        book.setId(1L);

        user = new UserEntity();
        user.setId(1L);

        rating = new RatingEntity();
        rating.setId(1L);
        rating.setBook(book);
        rating.setUser(user);

        ratingDto = new RatingResponseDto();
        ratingDto.setId(1L);

        ratingsPage = new PageImpl<>(List.of(rating));

        paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setContent(List.of(ratingDto));
        paginatedResponse.setTotalElements(1);
    }

    // getRatingById

    @Test
    void getRatingById_returnsRating() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingMapper.toDto(rating)).thenReturn(ratingDto);

        RatingResponseDto result = ratingService.getRatingById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getRatingById_throwsNotFoundException() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> ratingService.getRatingById(99L));
    }

    // getAllRatings

    @Test
    void getAllRatings_returnsPagedResults() {
        when(ratingRepository.findAll(any(Pageable.class))).thenReturn(ratingsPage);
        when(ratingMapper.toPaginatedDto(ratingsPage)).thenReturn(paginatedResponse);

        PaginatedResponse<RatingResponseDto> result = ratingService.getAllRatings(new RatingFilterDto());

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
    }

    // getRatingsByUserId

    @Test
    void getRatingsByUserId_returnsRatings() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ratingRepository.findAllByUser(any(UserEntity.class), any(Pageable.class))).thenReturn(ratingsPage);
        when(ratingMapper.toPaginatedDto(ratingsPage)).thenReturn(paginatedResponse);

        PaginatedResponse<RatingResponseDto> result = ratingService.getRatingsByUserId(1L, new RatingFilterDto());

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getRatingsByUserId_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> ratingService.getRatingsByUserId(99L, new RatingFilterDto()));
    }

    // getRatingsByBookId
    @Test
    void getRatingsByBookId_returnsRatings() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(ratingRepository.findAllByBook(any(BookEntity.class), any(Pageable.class))).thenReturn(ratingsPage);
        when(ratingMapper.toPaginatedDto(ratingsPage)).thenReturn(paginatedResponse);

        PaginatedResponse<RatingResponseDto> result = ratingService.getRatingsByBookId(1L, new RatingFilterDto());

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getRatingsByBookId_throwsNotFoundException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> ratingService.getRatingsByBookId(99L, new RatingFilterDto()));
    }

    // postRating

    @Test
    void postRating_savesRating_andUpdatesBookStats() {
        RatingRequestDto input = new RatingRequestDto();
        input.setBookId(1L);
        input.setUserId(1L);

        when(ratingMapper.toEntity(input, null)).thenReturn(rating);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ratingRepository.existsByBookAndUser(any(BookEntity.class), any(UserEntity.class))).thenReturn(false);
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingRepository.calculateAverageRatingByBook(book)).thenReturn(BigDecimal.valueOf(4.5));
        when(ratingRepository.countByBook(book)).thenReturn(1L);
        when(bookRepository.save(book)).thenReturn(book);
        when(ratingMapper.toDto(rating)).thenReturn(ratingDto);

        RatingResponseDto result = ratingService.postRating(input);

        verify(ratingRepository).save(rating);
        verify(bookRepository).save(book);
        assertEquals(book, rating.getBook());
        assertEquals(user, rating.getUser());
        assertEquals(ratingDto, result);
    }

    @Test
    void postRating_bookNotFound_throwsNotFoundException() {
        RatingRequestDto input = new RatingRequestDto();
        input.setBookId(99L);
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> ratingService.postRating(input));
    }

    @Test
    void postRating_userNotFound_throwsNotFoundException() {
        RatingRequestDto input = new RatingRequestDto();
        input.setBookId(1L);
        input.setUserId(99L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> ratingService.postRating(input));
    }

    @Test
    void postRating_alreadyRated_throwsConflictException() {
        RatingRequestDto input = new RatingRequestDto();
        input.setBookId(1L);
        input.setUserId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ratingRepository.existsByBookAndUser(book, user)).thenReturn(true);

        assertThrows(ConflictException.class, () -> ratingService.postRating(input));
    }

    // updateRating

    @Test
    void updateRating_UpdatesRating() {
        RatingUpdateRequestDto input = new RatingUpdateRequestDto();
        input.setReview("Test");

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingMapper.toEntity(input, rating)).thenReturn(rating);
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingRepository.calculateAverageRatingByBook(book)).thenReturn(BigDecimal.valueOf(4.5));
        when(ratingRepository.countByBook(book)).thenReturn(1L);
        when(bookRepository.save(book)).thenReturn(book);
        when(ratingMapper.toDto(rating)).thenReturn(ratingDto);

        RatingResponseDto result = ratingService.updateRating(1L, input);

        verify(ratingRepository).save(rating);
        verify(bookRepository).save(book);
        assertEquals(ratingDto, result);
    }

    @Test
    void updateRating_throwsNotFoundException() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> ratingService.updateRating(99L, new RatingUpdateRequestDto()));
    }

    // deleteRating

    @Test
    void deleteRating_deletesEntity() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingRepository.calculateAverageRatingByBook(book)).thenReturn(BigDecimal.valueOf(4.5));
        when(ratingRepository.countByBook(book)).thenReturn(1L);
        when(bookRepository.save(book)).thenReturn(book);

        ratingService.deleteRating(1L);


        verify(ratingRepository).delete(rating);
    }

    @Test
    void deleteRating_throwsNotFoundException() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> ratingService.deleteRating(99L));
    }
}
