package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.genre.GenreRequestDto;
import com.terridb.pagefolk.dtos.genre.GenreResponseDto;
import com.terridb.pagefolk.services.GenreService;
import com.terridb.pagefolk.utils.UrlUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<GenreResponseDto> postGenre(@Valid @RequestBody GenreRequestDto input) {
        GenreResponseDto dto = genreService.postGenre(input);

        return ResponseEntity
                .created(UrlUtil.generateUri("genres", dto.getId()))
                .body(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GenreResponseDto> updateGenre(@Valid @RequestBody GenreRequestDto input, @PathVariable Long id) {
        return ResponseEntity.ok(genreService.updateGenre(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
