package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.author.AuthorFilterDto;
import com.terridb.pagefolk.dtos.author.AuthorRequestDto;
import com.terridb.pagefolk.dtos.author.AuthorResponseDto;
import com.terridb.pagefolk.dtos.book.BookResponseDto;
import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.services.AuthorService;
import com.terridb.pagefolk.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<AuthorResponseDto>> getAllAuthors(AuthorFilterDto filter) {
        PaginatedResponse<AuthorResponseDto> result = authorService.getAllAuthors(
                filter.getSearch(),
                filter.getPage(),
                filter.getSize(),
                filter.getSortBy(),
                filter.getDirection()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<PaginatedResponse<BookResponseDto>> getBooksByAuthorId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(authorService.getBooksByAuthorId(id, page, size));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> postAuthor(@Valid @RequestBody AuthorRequestDto input) {
        AuthorResponseDto dto = authorService.postAuthor(input);

        return ResponseEntity
                .created(UrlUtil.generateUri("authors", dto.getId()))
                .body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> updateAuthor(@Valid @RequestBody AuthorRequestDto input, @PathVariable Long id) {
        return ResponseEntity.ok(authorService.updateAuthor(id, input));
    }

    @PatchMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> softDeleteAuthor(@PathVariable Long id) {
        authorService.softDeleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/revive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> reviveAuthor(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.reviveAuthor(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
