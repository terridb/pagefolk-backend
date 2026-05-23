package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.book.BookRequestDto;
import com.terridb.pagefolk.dtos.book.BookResponseDto;
import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.entities.AuthorEntity;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.entities.BookSeriesEntity;
import com.terridb.pagefolk.entities.GenreEntity;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.BookMapper;
import com.terridb.pagefolk.repositories.AuthorRepository;
import com.terridb.pagefolk.repositories.BookRepository;
import com.terridb.pagefolk.repositories.BookSeriesRepository;
import com.terridb.pagefolk.repositories.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Mock
    AuthorRepository authorRepository;

    @Mock
    GenreRepository genreRepository;

    @Mock
    BookSeriesRepository bookSeriesRepository;

    @InjectMocks
    BookService bookService;

    BookEntity book;
    BookResponseDto bookDto;
    Page<BookEntity> booksPage;
    PaginatedResponse<BookResponseDto> paginatedResponse;

    @BeforeEach
    void setUp() {
        book = new BookEntity();
        book.setId(1L);
        book.setTitle("The Hobbit");

        bookDto = new BookResponseDto();
        bookDto.setId(1L);
        bookDto.setTitle("The Hobbit");

        booksPage = new PageImpl<>(List.of(book));

        paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setContent(List.of(bookDto));
        paginatedResponse.setTotalElements(1);
    }

    @Test
    void getAllBooks_returnsPagedResults() {
        when(bookRepository.searchBooksWithFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(booksPage);
        when(bookMapper.toPaginatedDto(booksPage)).thenReturn(paginatedResponse);

        PaginatedResponse<BookResponseDto> result = bookService.getAllBooks(
                null, 0, 20, "title", "ASC", null, null, null, null, null, null);

        assertEquals(1, result.getContent().size());
        assertEquals("The Hobbit", result.getContent().getFirst().getTitle());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllBooks_negativePage_clampsToZero() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(bookRepository.searchBooksWithFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(booksPage);
        when(bookMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        bookService.getAllBooks(null, -5, 20, "title", "ASC", null, null, null, null, null, null);

        verify(bookRepository).searchBooksWithFilters(any(), any(), any(), any(), any(), any(), any(), pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void getAllBooks_sizeZero_defaultsTo20() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(bookRepository.searchBooksWithFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(booksPage);
        when(bookMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        bookService.getAllBooks(null, 0, 0, "title", "ASC", null, null, null, null, null, null);

        verify(bookRepository).searchBooksWithFilters(any(), any(), any(), any(), any(), any(), any(), pageableCaptor.capture());
        assertEquals(20, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getAllBooks_sizeOverMax_defaultsTo20() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(bookRepository.searchBooksWithFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(booksPage);
        when(bookMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        bookService.getAllBooks(null, 0, 101, "title", "ASC", null, null, null, null, null, null);

        verify(bookRepository).searchBooksWithFilters(any(), any(), any(), any(), any(), any(), any(), pageableCaptor.capture());
        assertEquals(20, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getAllBooks_lowercasesGenres() {
        ArgumentCaptor<List<String>> genresCaptor = ArgumentCaptor.captor();
        when(bookRepository.searchBooksWithFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(booksPage);
        when(bookMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        bookService.getAllBooks(null, 0, 20, "title", "ASC", List.of("Fantasy", "HORROR"), null, null, null, null, null);

        verify(bookRepository).searchBooksWithFilters(any(), any(), genresCaptor.capture(), any(), any(), any(), any(), any());
        assertEquals(new HashSet<>(List.of("fantasy", "horror")), new HashSet<>(genresCaptor.getValue()));
    }

    @Test
    void getAllBooks_nullGenres_passesNullToRepository() {
        ArgumentCaptor<List<String>> genresCaptor = ArgumentCaptor.captor();
        when(bookRepository.searchBooksWithFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(booksPage);
        when(bookMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        bookService.getAllBooks(null, 0, 20, "title", "ASC", null, null, null, null, null, null);

        verify(bookRepository).searchBooksWithFilters(any(), any(), genresCaptor.capture(), any(), any(), any(), any(), any());
        assertNull(genresCaptor.getValue());
    }

    @Test
    void getBookById_returnsBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookResponseDto result = bookService.getBookById(1L);

        assertEquals("The Hobbit", result.getTitle());
    }

    @Test
    void getBookById_returnsNotFound() {
        when(bookRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> bookService.getBookById(100L));
    }

    @Test
    void postBook_createsBookWithoutSeries() {
        BookRequestDto input = new BookRequestDto();
        input.setIsbn("9780261102217");
        input.setAuthorIds(Set.of(1L));
        input.setGenreIds(Set.of(1L));

        when(bookMapper.toEntity(input, null)).thenReturn(book);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(new AuthorEntity()));
        when(genreRepository.findById(1L)).thenReturn(Optional.of(new GenreEntity()));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookResponseDto result = bookService.postBook(input);

        verify(bookRepository).save(book);
        assertEquals("The Hobbit", result.getTitle());
    }

    @Test
    void postBook_createsBookWithSeries() {
        BookRequestDto input = new BookRequestDto();
        input.setIsbn("9780261102217");
        input.setAuthorIds(Set.of(1L));
        input.setGenreIds(Set.of(1L));
        input.setSeriesId(1L);

        when(bookMapper.toEntity(input, null)).thenReturn(book);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(new AuthorEntity()));
        when(genreRepository.findById(1L)).thenReturn(Optional.of(new GenreEntity()));
        when(bookSeriesRepository.findById(1L)).thenReturn(Optional.of(new BookSeriesEntity()));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookResponseDto result = bookService.postBook(input);

        verify(bookRepository).save(book);
        assertEquals("The Hobbit", result.getTitle());
    }

    @Test
    void postBook_throwsOnDuplicateIsbn() {
        BookRequestDto input = new BookRequestDto();
        input.setIsbn("9780261102217");
        input.setAuthorIds(Set.of(1L));
        input.setGenreIds(Set.of(1L));

        when(bookRepository.existsByIsbn("9780261102217")).thenReturn(true);

        assertThrows(ConflictException.class, () -> bookService.postBook(input));
    }

    @Test
    void updateBook() {
    }

    @Test
    void softDeleteBook() {
    }

    @Test
    void reviveBook() {
    }

    @Test
    void deleteBook() {
    }
}
