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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper, BookRepository bookRepository, BookMapper bookMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public PaginatedResponse<AuthorResponseDto> getAllAuthors(
            String search,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
//        //        Pagination
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        //        Sort
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AuthorEntity> authorsPage = authorRepository.searchAuthorsWithParams(search, pageable);
        return authorMapper.toPaginatedDto(authorsPage);
    }

    public AuthorResponseDto getAuthorById(Long id) {
        return authorMapper.toDto(
                authorRepository.findById(id)
                        .orElseThrow(() -> new RecordNotFoundException("Author with id " + id + " not found")));
    }

    public PaginatedResponse<BookResponseDto> getBooksByAuthorId(Long authorId, int page, int size) {
        if (!authorRepository.existsById(authorId)) {
            throw new RecordNotFoundException("Author with id " + authorId + " not found");
        }

        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        Pageable pageable = PageRequest.of(page, size);
        Page<BookEntity> booksPage = bookRepository.findAllByAuthors_IdAndDeletedDateIsNull(authorId, pageable);
        return bookMapper.toPaginatedDto(booksPage);
    }

    public AuthorResponseDto postAuthor(AuthorRequestDto input) {
        AuthorEntity entity = authorMapper.toEntity(input, null);
        return authorMapper.toDto(authorRepository.save(entity));
    }

    public AuthorResponseDto updateAuthor(Long authorId, AuthorRequestDto input) {
        AuthorEntity existingEntity = authorRepository.findById(authorId)
                .orElseThrow(() -> new RecordNotFoundException("Author with id " + authorId + " not found"));

        existingEntity.setFullName(input.getFullName());

        AuthorEntity savedEntity = authorRepository.save(existingEntity);
        return authorMapper.toDto(savedEntity);
    }

    public void softDeleteAuthor(Long id) {
        AuthorEntity existingEntity = authorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Author with id " + id + " not found"));

        if (existingEntity.getDeletedDate() != null) {
            throw new ConflictException("Author with id " + id + " is already soft-deleted");
        }

        existingEntity.setDeletedDate(LocalDate.now());
        authorRepository.save(existingEntity);
    }

    public AuthorResponseDto reviveAuthor(Long id) {
        AuthorEntity existingEntity = authorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Author with id " + id + " not found"));

        if (existingEntity.getDeletedDate() == null) {
            throw new ConflictException("Author with id " + id + " still exists and cannot be revived");
        }

        existingEntity.setDeletedDate(null);
        AuthorEntity savedEntity = authorRepository.save(existingEntity);
        return authorMapper.toDto(savedEntity);
    }

    public void deleteAuthor(Long id) {
        AuthorEntity existingEntity = authorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Author with id " + id + " not found"));

        if (existingEntity.getDeletedDate() == null) {
            throw new ConflictException("Author with id " + id + " should first be soft-deleted before full delete");
        }

        existingEntity.getBooks().forEach(book -> book.getAuthors().remove(existingEntity));

        authorRepository.delete(existingEntity);
    }
}
