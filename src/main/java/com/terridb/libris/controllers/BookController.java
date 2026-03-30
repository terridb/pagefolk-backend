package com.terridb.libris.controllers;

import com.terridb.libris.dtos.book.*;
import com.terridb.libris.services.BookService;
import com.terridb.libris.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<PaginatedBookResponse> getAllBooks(BookFilterDto filter) {
        PaginatedBookResponse result = bookService.getAllBooks(
                filter.getSearch(),
                filter.getPage(),
                filter.getSize(),
                filter.getSortBy(),
                filter.getDirection(),
                filter.getGenres(),
                filter.getAuthorIds(),
                filter.getSeriesId(),
                filter.getMinRating(),
                filter.getReleaseAfter(),
                filter.getReleaseBefore()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<BookResponseDto> postBook(@Valid @RequestBody BookRequestDto input) {
        BookResponseDto dto = bookService.postBook(input);

        return ResponseEntity
                .created(UrlUtil.generateUri("books", dto.getId()))
                .body(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookResponseDto> updateBook(@Valid @RequestBody BookPatchDto input, @PathVariable Long id) {
        return ResponseEntity.ok(bookService.updateBook(id, input));
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<BookResponseDto> softDeleteBook(@PathVariable Long id) {
        bookService.softDeleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/revive")
    public ResponseEntity<BookResponseDto> reviveBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.reviveBook(id));
    }
}
