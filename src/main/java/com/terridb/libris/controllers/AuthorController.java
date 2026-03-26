package com.terridb.libris.controllers;

import com.terridb.libris.dtos.author.AuthorFilterDto;
import com.terridb.libris.dtos.author.AuthorResponseDto;
import com.terridb.libris.dtos.author.PaginatedAuthorResponse;
import com.terridb.libris.services.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<PaginatedAuthorResponse> getAllBooks(AuthorFilterDto filter) {
        PaginatedAuthorResponse result = authorService.getAllAuthors(
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
}
