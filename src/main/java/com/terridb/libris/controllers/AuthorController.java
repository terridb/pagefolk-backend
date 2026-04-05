package com.terridb.libris.controllers;

import com.terridb.libris.dtos.author.AuthorFilterDto;
import com.terridb.libris.dtos.author.AuthorRequestDto;
import com.terridb.libris.dtos.author.AuthorResponseDto;
import com.terridb.libris.dtos.common.PaginatedResponse;
import com.terridb.libris.services.AuthorService;
import com.terridb.libris.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<AuthorResponseDto> postAuthor(@Valid @RequestBody AuthorRequestDto input) {
        AuthorResponseDto dto = authorService.postAuthor(input);

        return ResponseEntity
                .created(UrlUtil.generateUri("authors", dto.getId()))
                .body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> updateAuthor(@Valid @RequestBody AuthorRequestDto input, @PathVariable Long id) {
        return ResponseEntity.ok(authorService.updateAuthor(id, input));
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<AuthorResponseDto> softDeleteAuthor(@PathVariable Long id) {
        authorService.softDeleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/revive")
    public ResponseEntity<AuthorResponseDto> reviveAuthor(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.reviveAuthor(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
