package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.book.BookResponseDto;
import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.genre.GenreRequestDto;
import com.terridb.pagefolk.dtos.genre.GenreResponseDto;
import com.terridb.pagefolk.services.GenreService;
import com.terridb.pagefolk.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<GenreResponseDto>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponseDto> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<PaginatedResponse<BookResponseDto>> getBooksByGenreId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(genreService.getBooksByGenreId(id, page, size));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenreResponseDto> postGenre(@Valid @RequestBody GenreRequestDto input) {
        GenreResponseDto dto = genreService.postGenre(input);

        return ResponseEntity
                .created(UrlUtil.generateUri("genres", dto.getId()))
                .body(dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenreResponseDto> updateGenre(@Valid @RequestBody GenreRequestDto input, @PathVariable Long id) {
        return ResponseEntity.ok(genreService.updateGenre(id, input));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
