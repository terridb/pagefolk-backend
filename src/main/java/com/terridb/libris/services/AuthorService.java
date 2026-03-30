package com.terridb.libris.services;

import com.terridb.libris.dtos.author.AuthorRequestDto;
import com.terridb.libris.dtos.author.AuthorResponseDto;
import com.terridb.libris.dtos.author.PaginatedAuthorResponse;
import com.terridb.libris.entities.AuthorEntity;
import com.terridb.libris.exceptions.ConflictException;
import com.terridb.libris.exceptions.RecordNotFoundException;
import com.terridb.libris.mappers.AuthorMapper;
import com.terridb.libris.repositories.AuthorRepository;
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

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public PaginatedAuthorResponse getAllAuthors(
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

        if (existingEntity.getDeletedAt() != null) {
            throw new ConflictException("Author with id " + id + " is already soft-deleted");
        }

        existingEntity.setDeletedAt(LocalDate.now());
        authorRepository.save(existingEntity);
    }

    public AuthorResponseDto reviveAuthor(Long id) {
        AuthorEntity existingEntity = authorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Author with id " + id + " not found"));

        if (existingEntity.getDeletedAt() == null) {
            throw new ConflictException("Author with id " + id + " still exists and cannot be revived");
        }

        existingEntity.setDeletedAt(null);
        AuthorEntity savedEntity = authorRepository.save(existingEntity);
        return authorMapper.toDto(savedEntity);
    }
}
