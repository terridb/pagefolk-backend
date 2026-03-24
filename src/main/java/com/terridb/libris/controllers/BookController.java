package com.terridb.libris.controllers;

import com.terridb.libris.dtos.book.BookPatchDto;
import com.terridb.libris.dtos.book.BookRequestDto;
import com.terridb.libris.dtos.book.BookResponseDto;
import com.terridb.libris.dtos.book.PaginatedBookResponse;
import com.terridb.libris.services.BookService;
import com.terridb.libris.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<PaginatedBookResponse> getAllBooks(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(bookService.getAllBooks(search, page, size, sortBy, direction));
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
}
