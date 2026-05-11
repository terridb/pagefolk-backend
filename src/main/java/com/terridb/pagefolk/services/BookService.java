package com.terridb.pagefolk.services;

import com.terridb.pagefolk.dtos.book.BookPatchDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookSeriesRepository bookSeriesRepository;

    public BookService(BookRepository bookRepository, BookMapper bookMapper, AuthorRepository authorRepository, GenreRepository genreRepository, BookSeriesRepository bookSeriesRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookSeriesRepository = bookSeriesRepository;
    }

    //    Get all books + sorting, pagination, filters, search
    public PaginatedResponse<BookResponseDto> getAllBooks(
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

//        Set authors
        Set<AuthorEntity> authors = entity.getAuthors();
        for (Long authorId : input.getAuthorIds()) {
            AuthorEntity existingEntity = authorRepository.findById(authorId)
                    .orElseThrow(() -> new RecordNotFoundException("Author with id " + authorId + " not found"));
            authors.add(existingEntity);
        }
        entity.setAuthors(authors);

//        Set genres
        Set<GenreEntity> genres = entity.getGenres();
        for (Long genreId : input.getGenreIds()) {
            GenreEntity existingEntity = genreRepository.findById(genreId)
                    .orElseThrow(() -> new RecordNotFoundException("Genre with id " + genreId + " not found"));
            genres.add(existingEntity);
        }
        entity.setGenres(genres);

//        Set series
        if (input.getSeriesId() != null) {
            BookSeriesEntity existingEntity = bookSeriesRepository.findById(input.getSeriesId())
                    .orElseThrow(() -> new RecordNotFoundException("Series with id " + input.getSeriesId() + " not found"));
            entity.setSeries(existingEntity);
        }

        BookEntity savedEntity = bookRepository.save(entity);
        return bookMapper.toDto(savedEntity);
    }

    //    Update book-metadata
    public BookResponseDto updateBook(Long bookId, BookPatchDto input) {
        BookEntity existingEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " not found"));

        if (input.getTitle() != null ) {
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

    public void softDeleteBook(Long id) {
        BookEntity existingEntity = bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + id + " not found"));

        if (existingEntity.getDeletedAt() != null) {
            throw new ConflictException("Book with id " + id + " is already soft-deleted");
        }

        existingEntity.setDeletedAt(LocalDate.now());
        bookRepository.save(existingEntity);
    }

    public BookResponseDto reviveBook(Long id) {
        BookEntity existingEntity = bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + id + " not found"));

        if (existingEntity.getDeletedAt() == null) {
            throw new ConflictException("Book with id " + id + " still exists and cannot be revived");
        }

        existingEntity.setDeletedAt(null);
        BookEntity savedEntity = bookRepository.save(existingEntity);
        return bookMapper.toDto(savedEntity);
    }

    public void deleteBook(Long id) {
        BookEntity existingEntity = bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + id + " not found"));

        if (existingEntity.getDeletedAt() == null) {
            throw new ConflictException("Book with id " + id + " should first be soft-deleted before full delete");
        }

        existingEntity.getGenres().forEach(genre -> genre.getBooks().remove(existingEntity));
        existingEntity.getAuthors().forEach(author -> author.getBooks().remove(existingEntity));
        existingEntity.getSeries().getBooks().remove(existingEntity);
        existingEntity.getLists().forEach(list -> list.getBooks().remove(existingEntity));
        existingEntity.getProfilesWithFavorite().forEach(profile -> profile.getFavoriteBooks().remove(existingEntity));

        bookRepository.delete(existingEntity);
    }

//    todo add endpoint to see all soft-deleted books

    private void checkIsbnAvailability(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new ConflictException("ISBN is already in use");
        }
    }
}
