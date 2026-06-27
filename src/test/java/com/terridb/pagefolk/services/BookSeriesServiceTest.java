package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.bookseries.BookSeriesRequestDto;
import com.terridb.pagefolk.dtos.bookseries.BookSeriesResponseDto;
import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.BookSeriesEntity;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.SeriesMapper;
import com.terridb.pagefolk.repositories.BookRepository;
import com.terridb.pagefolk.repositories.BookSeriesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookSeriesServiceTest {

    @Mock
    private BookSeriesRepository bookSeriesRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private SeriesMapper seriesMapper;

    @InjectMocks
    private BookSeriesService bookSeriesService;

    BookSeriesEntity series;
    BookSeriesResponseDto seriesDto;
    Page<BookSeriesEntity> seriesPage;
    PaginatedResponse<BookSeriesResponseDto> paginatedResponse;

    @BeforeEach
    void setUp() {
        series = new BookSeriesEntity();
        series.setId(1L);
        series.setTitle("The Hungergames");

        seriesDto = new BookSeriesResponseDto();
        seriesDto.setId(1L);
        seriesDto.setTitle("The Hungergames");

        seriesPage = new PageImpl<>(List.of(series));

        paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setContent(List.of(seriesDto));
        paginatedResponse.setTotalElements(1);
    }

    // getAllBookSeries

    @Test
    void getAllBookSeries_returnsPagedResults() {
        when(bookSeriesRepository.findAll(any(Pageable.class))).thenReturn(seriesPage);
        when(seriesMapper.toPaginatedDto(seriesPage)).thenReturn(paginatedResponse);

        PaginatedResponse<BookSeriesResponseDto> result = bookSeriesService.getAllBookSeries(0, 20);

        assertEquals(1, result.getContent().size());
        assertEquals("The Hungergames", result.getContent().getFirst().getTitle());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllBookSeries_negativePage_clampsToZero() {
        when(bookSeriesRepository.findAll(any(Pageable.class))).thenReturn(seriesPage);
        when(seriesMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        bookSeriesService.getAllBookSeries(-1, 20);

        verify(bookSeriesRepository).findAll(PageRequest.of(0, 20));
    }

    @Test
    void getAllBookSeries_sizeZero_defaultsTo20() {
        when(bookSeriesRepository.findAll(any(Pageable.class))).thenReturn(seriesPage);
        when(seriesMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        bookSeriesService.getAllBookSeries(0, 0);

        verify(bookSeriesRepository).findAll(PageRequest.of(0, 20));
    }

    @Test
    void getAllBookSeries_sizeOverMax_defaultsTo20() {
        when(bookSeriesRepository.findAll(any(Pageable.class))).thenReturn(seriesPage);
        when(seriesMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        bookSeriesService.getAllBookSeries(0, 101);

        verify(bookSeriesRepository).findAll(PageRequest.of(0, 20));
    }

    // getBookSeriesById

    @Test
    void getBookSeriesById_returnsSeries() {
        when(bookSeriesRepository.findById(1L)).thenReturn(Optional.of(series));
        when(seriesMapper.toDto(series)).thenReturn(seriesDto);

        BookSeriesResponseDto result = bookSeriesService.getBookSeriesById(1L);

        assertEquals("The Hungergames", result.getTitle());
    }

    @Test
    void getBookSeriesById_throwsNotFoundException() {
        when(bookSeriesRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> bookSeriesService.getBookSeriesById(100L));
    }

    // postBookSeries

    @Test
    void postBookSeries_createsSeries() {
        BookSeriesRequestDto input = new  BookSeriesRequestDto();
        input.setTitle("The Hungergames");

        when(seriesMapper.toEntity(input, null)).thenReturn(series);
        when(bookSeriesRepository.save(series)).thenReturn(series);
        when(seriesMapper.toDto(series)).thenReturn(seriesDto);

        BookSeriesResponseDto result = bookSeriesService.postBookSeries(input);

        verify(bookSeriesRepository).save(series);
        assertEquals("The Hungergames", result.getTitle());
    }

    @Test
    void postBookSeries_withBookIds_assignsBooksToSeries() {
        BookSeriesRequestDto input = new BookSeriesRequestDto();
        input.setTitle("The Hungergames");
        input.setBookIds(Set.of(1L));

        BookEntity book = new BookEntity();
        book.setId(1L);

        when(seriesMapper.toEntity(input, null)).thenReturn(series);
        when(bookSeriesRepository.save(series)).thenReturn(series);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(seriesMapper.toDto(series)).thenReturn(seriesDto);

        bookSeriesService.postBookSeries(input);

        verify(bookSeriesRepository).save(series);
        assertEquals(series, book.getSeries());
    }

    @Test
    void postBookSeries_withTakenBookIds_throwsConflictException() {
        BookSeriesRequestDto input = new BookSeriesRequestDto();
        input.setTitle("The Hungergames");
        input.setBookIds(Set.of(1L));

        BookSeriesEntity series2 = new BookSeriesEntity();
        series2.setId(2L);

        BookEntity book = new BookEntity();
        book.setId(1L);
        book.setSeries(series2);

        when(seriesMapper.toEntity(input, null)).thenReturn(series);
        when(bookSeriesRepository.save(series)).thenReturn(series);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(ConflictException.class, () -> bookSeriesService.postBookSeries(input));
    }

    // updateBookSeries

    @Test
    void updateBookSeries_throwsNotFoundException() {
        when(bookSeriesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> bookSeriesService.updateBookSeries(99L, new BookSeriesRequestDto()));
    }

    @Test
    void updateBookSeries_withDescription_updatesDescription() {
        BookSeriesRequestDto input = new BookSeriesRequestDto();
        input.setDescription("A dystopian series");

        when(bookSeriesRepository.findById(1L)).thenReturn(Optional.of(series));
        when(bookSeriesRepository.save(series)).thenReturn(series);
        when(seriesMapper.toDto(series)).thenReturn(seriesDto);

        bookSeriesService.updateBookSeries(1L, input);

        verify(bookSeriesRepository).save(series);
        assertEquals("A dystopian series", series.getDescription());
    }

    @Test
    void updateBookSeries_withBookIds_removesOldKeepsExistingAndAddsNew() {
        BookSeriesRequestDto input = new BookSeriesRequestDto();
        input.setBookIds(Set.of(1L, 3L));

        BookEntity book1 = new BookEntity();
        book1.setId(1L);
        BookEntity book2 = new BookEntity();
        book2.setId(2L);
        BookEntity book3 = new BookEntity();
        book3.setId(3L);

        series.getBooks().add(book1);
        series.getBooks().add(book2);

        when(bookSeriesRepository.findById(1L)).thenReturn(Optional.of(series));
        when(bookSeriesRepository.save(series)).thenReturn(series);
        when(bookRepository.findById(3L)).thenReturn(Optional.of(book3));
        when(seriesMapper.toDto(series)).thenReturn(seriesDto);

        bookSeriesService.updateBookSeries(1L, input);

        verify(bookSeriesRepository).save(series);
        verify(bookRepository).save(book2);
        assertNull(book2.getSeries());
        assertEquals(series, book3.getSeries());
    }

    @Test
    void updateBookSeries_updatesTitleAndReturnsDto() {
        BookSeriesRequestDto input = new BookSeriesRequestDto();
        input.setTitle("The Not So Hungergames");

        when(bookSeriesRepository.findById(1L)).thenReturn(Optional.of(series));
        when(bookSeriesRepository.save(series)).thenReturn(series);
        when(seriesMapper.toDto(series)).thenReturn(seriesDto);

        bookSeriesService.updateBookSeries(1L, input);

        verify(bookSeriesRepository).save(series);
        assertEquals("The Not So Hungergames", series.getTitle());
    }

    @Test
    void updateBookSeries_withBookIds_updatesBooksInSeries() {
        BookSeriesRequestDto input = new BookSeriesRequestDto();
        input.setBookIds(Set.of(1L, 2L));

        BookEntity book1 = new BookEntity();
        book1.setId(1L);
        BookEntity book2 = new BookEntity();
        book2.setId(2L);

        when(bookSeriesRepository.findById(1L)).thenReturn(Optional.of(series));
        when(bookSeriesRepository.save(series)).thenReturn(series);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));
        when(seriesMapper.toDto(series)).thenReturn(seriesDto);

        bookSeriesService.updateBookSeries(1L, input);

        verify(bookSeriesRepository).save(series);
        assertEquals(series, book1.getSeries());
        assertEquals(series, book2.getSeries());
    }

    // deleteBookSeries

    @Test
    void deleteBookSeries_deletesEntity() {
        when(bookSeriesRepository.findById(1L)).thenReturn(Optional.of(series));

        bookSeriesService.deleteBookSeries(1L);

        verify(bookSeriesRepository).delete(series);
    }

    @Test
    void deleteBookSeries_throwsNotFoundException() {
        when(bookSeriesRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> bookSeriesService.deleteBookSeries(100L));
    }
}