package com.terridb.libris.services;

import com.terridb.libris.dtos.book.BookResponseDto;
import com.terridb.libris.entities.BookEntity;
import com.terridb.libris.exceptions.RecordNotFoundException;
import com.terridb.libris.mappers.BookMapper;
import com.terridb.libris.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookResponseDto> getAllBooks() {
        List<BookEntity> entities = bookRepository.findAll();

        return bookMapper.toDto(entities);
    }

    public BookResponseDto getBookById(Long id) {
        return bookMapper.toDto(
                bookRepository.findById(id)
                        .orElseThrow(() -> new RecordNotFoundException("Book with id " + id + " not found")));
    }
}
