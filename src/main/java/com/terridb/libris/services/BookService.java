package com.terridb.libris.services;

import com.terridb.libris.dtos.book.BookPatchDto;
import com.terridb.libris.dtos.book.BookRequestDto;
import com.terridb.libris.dtos.book.BookResponseDto;
import com.terridb.libris.dtos.book.PaginatedBookResponse;
import com.terridb.libris.entities.BookEntity;
import com.terridb.libris.exceptions.ConflictException;
import com.terridb.libris.exceptions.RecordNotFoundException;
import com.terridb.libris.mappers.BookMapper;
import com.terridb.libris.repositories.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    //    Get all books + sorting, pagination, filters, search
    public PaginatedBookResponse getAllBooks(
            String search,
            int page,
            int size,
            String sortBy,
            String direction,
            List<String> genres,
            List<Long> authorIds,
            Long seriesId,
            Double minRating,
            LocalDate releaseAfter,
            LocalDate releaseBefore
    ) {

//        Pagination
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

//        Genres
        if (genres != null) genres = genres.stream()
                .map(String::toLowerCase)
                .toList();

//        Sort
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BookEntity> booksPage = bookRepository.searchBooksWithFilters(
                search,
                authorIds,
                genres,
                seriesId,
                releaseAfter,
                releaseBefore,
                minRating,
                pageable
        );

        return bookMapper.toPaginatedDto(booksPage);
    }


    public BookResponseDto getBookById(Long id) {
        return bookMapper.toDto(
                bookRepository.findById(id)
                        .orElseThrow(() -> new RecordNotFoundException("Book with id " + id + " not found")));
    }

    public BookResponseDto postBook(BookRequestDto input) {
        checkIsbnAvailability(input.getIsbn());
        BookEntity entity = bookMapper.toEntity(input, null);
        BookEntity savedEntity = bookRepository.save(entity);

        return bookMapper.toDto(savedEntity);
    }

    //    Update book-metadata
    public BookResponseDto updateBook(Long bookId, BookPatchDto input) {
        BookEntity existingEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " not found"));

        if (input.getTitle() != null) {
            existingEntity.setTitle(input.getTitle());
        }

        if (input.getDescription() != null) {
            existingEntity.setDescription(input.getDescription());
        }

        if (input.getIsbn() != null && !input.getIsbn().equals(existingEntity.getIsbn())) {
            checkIsbnAvailability(input.getIsbn());
            existingEntity.setIsbn(input.getIsbn());
        }

        if (input.getReleaseDate() != null) {
            existingEntity.setReleaseDate(input.getReleaseDate());
        }

        BookEntity savedEntity = bookRepository.save(existingEntity);
        return bookMapper.toDto(savedEntity);
    }

    private void checkIsbnAvailability(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new ConflictException("ISBN is already in use");
        }
    }
}
