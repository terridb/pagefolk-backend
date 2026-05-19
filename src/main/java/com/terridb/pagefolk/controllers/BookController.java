package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.book.BookPatchDto;
import com.terridb.pagefolk.dtos.book.BookFilterDto;
import com.terridb.pagefolk.dtos.book.BookRequestDto;
import com.terridb.pagefolk.dtos.book.BookResponseDto;
import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.services.BookService;
import com.terridb.pagefolk.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<BookResponseDto>> getAllBooks(BookFilterDto filter) {
        PaginatedResponse<BookResponseDto> result = bookService.getAllBooks(
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> postBook(@Valid @RequestBody BookRequestDto input) {
        BookResponseDto dto = bookService.postBook(input);

        return ResponseEntity
                .created(UrlUtil.generateUri("books", dto.getId()))
                .body(dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> updateBook(@Valid @RequestBody BookPatchDto input, @PathVariable Long id) {
        return ResponseEntity.ok(bookService.updateBook(id, input));
    }

    @PatchMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> softDeleteBook(@PathVariable Long id) {
        bookService.softDeleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/revive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> reviveBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.reviveBook(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
