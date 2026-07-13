package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.author.AuthorRequestDto;
import com.terridb.pagefolk.dtos.author.AuthorResponseDto;
import com.terridb.pagefolk.dtos.book.BookResponseDto;
import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.entities.AuthorEntity;
import com.terridb.pagefolk.entities.BookEntity;
import com.terridb.pagefolk.exceptions.ConflictException;
import com.terridb.pagefolk.exceptions.RecordNotFoundException;
import com.terridb.pagefolk.mappers.AuthorMapper;
import com.terridb.pagefolk.mappers.BookMapper;
import com.terridb.pagefolk.repositories.AuthorRepository;
import com.terridb.pagefolk.repositories.BookRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private AuthorService authorService;

    AuthorEntity author;
    AuthorResponseDto authorDto;
    Page<AuthorEntity> authorPage;
    PaginatedResponse<AuthorResponseDto> paginatedResponse;

    @BeforeEach
    void setUp() {
        author = new AuthorEntity();
        author.setId(1L);
        author.setFullName("John Doe");

        authorDto = new AuthorResponseDto();
        authorDto.setId(1L);
        authorDto.setFullName("John Doe");

        authorPage = new PageImpl<>(List.of(author));

        paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setContent(List.of(authorDto));
        paginatedResponse.setTotalElements(1);
    }

    // getAllAuthors

    @Test
    void getAllAuthors_returnsPagedResults() {
        when(authorRepository.searchAuthorsWithParams(any(), any()))
                .thenReturn(authorPage);
        when(authorMapper.toPaginatedDto(authorPage)).thenReturn(paginatedResponse);

        PaginatedResponse<AuthorResponseDto> result = authorService.getAllAuthors(
                null, 0, 20, "fullName", "ASC");

        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().getFirst().getFullName());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllAuthors_negativePage_clampsToZero() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(authorRepository.searchAuthorsWithParams(any(), any()))
                .thenReturn(authorPage);
        when(authorMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        authorService.getAllAuthors(null, -1, 20, "fullName", "ASC");

        verify(authorRepository).searchAuthorsWithParams(any(), pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void getAllAuthors_sizeZero_defaultsTo20() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(authorRepository.searchAuthorsWithParams(any(), any()))
                .thenReturn(authorPage);
        when(authorMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        authorService.getAllAuthors(null, 0, 0, "fullName", "ASC");

        verify(authorRepository).searchAuthorsWithParams(any(), pageableCaptor.capture());
        assertEquals(20, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getAllAuthors_sizeOverMax_defaultsTo20() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(authorRepository.searchAuthorsWithParams(any(), any()))
                .thenReturn(authorPage);
        when(authorMapper.toPaginatedDto(any())).thenReturn(paginatedResponse);

        authorService.getAllAuthors(null, 0, 101, "fullName", "ASC");

        verify(authorRepository).searchAuthorsWithParams(any(), pageableCaptor.capture());
        assertEquals(20, pageableCaptor.getValue().getPageSize());
    }

    // getAuthorById

    @Test
    void getAuthorById_returnsAuthor() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        AuthorResponseDto result = authorService.getAuthorById(1L);

        assertEquals("John Doe", result.getFullName());
    }

    @Test
    void getAuthorById_throwsNotFoundException() {
        when(authorRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> authorService.getAuthorById(100L));
    }

    // getBooksByAuthorId

    @Test
    void getBooksByAuthorId_returnsPagedResults() {
        BookEntity book = new BookEntity();
        book.setId(1L);
        book.setTitle("Beach Read");
        Page<BookEntity> bookPage = new PageImpl<>(List.of(book));

        BookResponseDto bookDto = new BookResponseDto();
        bookDto.setId(1L);
        bookDto.setTitle("Beach Read");
        PaginatedResponse<BookResponseDto> bookResponse = new PaginatedResponse<>();
        bookResponse.setContent(List.of(bookDto));
        bookResponse.setTotalElements(1);

        when(authorRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.findAllByAuthors_IdAndDeletedDateIsNull(any(), any())).thenReturn(bookPage);
        when(bookMapper.toPaginatedDto(bookPage)).thenReturn(bookResponse);

        PaginatedResponse<BookResponseDto> result = authorService.getBooksByAuthorId(1L, 0, 20);

        verify(bookRepository).findAllByAuthors_IdAndDeletedDateIsNull(any(), any());
        assertEquals(1, result.getContent().size());
        assertEquals("Beach Read", result.getContent().getFirst().getTitle());
    }

    @Test
    void getBooksByAuthorId_authorNotFound_throwsNotFoundException() {
        when(authorRepository.existsById(100L)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> authorService.getBooksByAuthorId(100L, 0, 20));
    }

    @Test
    void getBooksByAuthorId_negativePage_clampsToZero() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(authorRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.findAllByAuthors_IdAndDeletedDateIsNull(any(), any())).thenReturn(new PageImpl<>(List.of()));
        when(bookMapper.toPaginatedDto(any())).thenReturn(new PaginatedResponse<>());

        authorService.getBooksByAuthorId(1L, -1, 20);

        verify(bookRepository).findAllByAuthors_IdAndDeletedDateIsNull(any(), pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void getBooksByAuthorId_sizeOverMax_defaultsTo20() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(authorRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.findAllByAuthors_IdAndDeletedDateIsNull(any(), any())).thenReturn(new PageImpl<>(List.of()));
        when(bookMapper.toPaginatedDto(any())).thenReturn(new PaginatedResponse<>());

        authorService.getBooksByAuthorId(1L, 0, 101);

        verify(bookRepository).findAllByAuthors_IdAndDeletedDateIsNull(any(), pageableCaptor.capture());
        assertEquals(20, pageableCaptor.getValue().getPageSize());
    }

    // postAuthor

    @Test
    void postAuthor_createsAuthor() {
        AuthorRequestDto input = new AuthorRequestDto();
        input.setFullName("John Doe");

        when(authorMapper.toEntity(input, null)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        AuthorResponseDto result = authorService.postAuthor(input);

        verify(authorRepository).save(author);
        assertEquals("John Doe", result.getFullName());
    }

    // updateAuthor

    @Test
    void updateAuthor_updatesFullNameAndReturnsDto() {
        AuthorRequestDto input = new AuthorRequestDto();
        input.setFullName("Jane Doe");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        authorService.updateAuthor(1L, input);

        verify(authorRepository).save(author);
        assertEquals("Jane Doe", author.getFullName());
    }

    @Test
    void updateAuthor_throwsNotFoundException() {
        AuthorRequestDto input = new AuthorRequestDto();
        input.setFullName("Jane Doe");

        when(authorRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> authorService.updateAuthor(100L, input));
    }

    // softDeleteAuthor

    @Test
    void softDeleteAuthor_setsDeletedDate() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        authorService.softDeleteAuthor(1L);

        verify(authorRepository).save(author);
        assertEquals(LocalDate.now(), author.getDeletedDate());
    }

    @Test
    void softDeleteAuthor_alreadyDeleted_throwsConflictException() {
        author.setDeletedDate(LocalDate.now());

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        assertThrows(ConflictException.class, () -> authorService.softDeleteAuthor(1L));
    }

    @Test
    void softDeleteAuthor_throwsNotFoundException() {
        when(authorRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> authorService.softDeleteAuthor(100L));
    }

    // reviveAuthor

    @Test
    void reviveAuthor_clearsDeletedDate() {
        author.setDeletedDate(LocalDate.now());

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        authorService.reviveAuthor(1L);

        verify(authorRepository).save(author);
        assertNull(author.getDeletedDate());
    }

    @Test
    void reviveAuthor_notDeleted_throwsConflictException() {
        author.setDeletedDate(null);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        assertThrows(ConflictException.class, () -> authorService.reviveAuthor(1L));
    }

    @Test
    void reviveAuthor_throwsNotFoundException() {
        when(authorRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> authorService.reviveAuthor(100L));
    }

    // deleteAuthor

    @Test
    void deleteAuthor_deletesEntity() {
        author.setDeletedDate(LocalDate.now());

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        authorService.deleteAuthor(1L);

        verify(authorRepository).delete(author);
    }

    @Test
    void deleteAuthor_notSoftDeleted_throwsConflictException() {
        author.setDeletedDate(null);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        assertThrows(ConflictException.class, () -> authorService.deleteAuthor(1L));
    }

    @Test
    void deleteAuthor_throwsNotFoundException() {
        when(authorRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> authorService.deleteAuthor(100L));
    }
}
